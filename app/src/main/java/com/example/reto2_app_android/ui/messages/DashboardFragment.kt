package com.example.reto2_app_android.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.UserNew
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.databinding.FragmentDashboardBinding
import com.example.reto2_app_android.utils.Resource

private const val ARG_CHAT = "chat"

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val TAG = "SocketActivity"
    private lateinit var messageAdapter: DashboardAdapter
    private val viewModel: DashboardViewModel by viewModels { DashboardViewModelFactory() }
    private var chat: ChatResponse_Chat? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chat = it.getParcelable(ARG_CHAT, ChatResponse_Chat::class.java)

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        messageAdapter = DashboardAdapter()
        binding.recyclerGroupChat.adapter = messageAdapter

        binding.buttonGroupChatSend.isEnabled = false
        val recyclerView = ViewBindings.findChildViewById<RecyclerView>(root, R.id.recycler_group_chat)
        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(context)

        }

        onConnectedChange(binding)
        onMessagesChange()
        buttonsListeners(binding)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onConnectedChange(binding: FragmentDashboardBinding) {
        viewModel.connected.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.buttonGroupChatSend.isEnabled = it.data!!
                }

                Resource.Status.ERROR -> {
                    // TODO sin gestionarlo en el VM. Y si envia en una sala que ya no esta? a tratar
                    Log.d(TAG, "error al conectar...")
                    binding.buttonGroupChatSend.isEnabled = it.data!!
                }

                Resource.Status.LOADING -> {
                    binding.buttonGroupChatSend.isEnabled = it.data!!
                }
            }
        }
    }
    private fun onMessagesChange() {
        viewModel.messages.observe(viewLifecycleOwner) {
            Log.d(TAG, "messages change")
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Log.d(TAG, "messages observe success")
                    if (!it.data.isNullOrEmpty()) {
                        messageAdapter.submitList(it.data)
                        messageAdapter.notifyDataSetChanged()
                    }
                }

                Resource.Status.ERROR -> {
                    Log.d(TAG, "messages observe error")
                    //Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }

                Resource.Status.LOADING -> {
                    // de momento
                    Log.d(TAG, "messages observe loading")
                    //val toast = Toast.makeText(this, "Cargando..", Toast.LENGTH_LONG)
                    //toast.setGravity(Gravity.TOP, 0, 0)
                    //toast.show()
                }
            }
        }
    }

    private fun buttonsListeners(binding: FragmentDashboardBinding) {
        binding.btnConnect.setOnClickListener() {
            Log.i("Aimar", "Button connect")
            viewModel.startSocket()
        }
        binding.buttonGroupChatSend.setOnClickListener() {
            Log.i("Aimar", "Buton send")
            val message = binding.editGroupChatMessage.text.toString();
            viewModel.onSendMessage(message)
            binding.editGroupChatMessage.text.clear()
        }
    }
}