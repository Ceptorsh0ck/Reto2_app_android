# ELORCHAT

![Chat App](app/src/main/res/drawable/elorchat_recortada.png)

## Descripción del Proyecto

ElorChat es una aplicación de chat que permite la comunicación entre alumnos y profesores.

## Built With

Para el apartado de Android se ha utilizado:

- [Android Studio](https://developer.android.com/studio)
- [Kotlin](https://kotlinlang.org/)
- [Room](https://developer.android.com/jetpack/androidx/releases/room)
- [Retrofit](https://square.github.io/retrofit/)
- [Socket.io](https://socket.io/)
- [Firebase](https://firebase.google.com/)

## Funcionalidades para Alumnos

- Crear un grupo de chat libre.
- Borrar un grupo de chat libre del que sea el creador.
- Unirse a un grupo de chat libre.
- Abandonar un grupo de chat libre.
- Recibir notificaciones sobre mensajes y cambios en grupos.
- Enviar y recibir mensajes de un grupo libre.

## Funcionalidades para Profesores

- Crear un grupo privado.
- Borrar un grupo privado del que sea el creador.
- Borrar un grupo de chat libre.
- Asignar y desasignar alumnos a un grupo privado.

## Tipos de Mensajes

Los mensajes pueden ser de:

- Texto plano.
- Imagen obtenida por la cámara del dispositivo.
- Fichero obtenido del dispositivo.
- Posición GPS del móvil.

## Visualización de Mensajes

Los mensajes pueden contener:

- Texto plano: visualización del texto y su emisor.
- Imagen: muestra la imagen en el dispositivo.
- Fichero: se guarda en el dispositivo.
- Posición GPS: se muestra en coordenadas, pero al hacer click lleva a maps.

## Seguridad

Las contraseñas viajan cifradas por la red mediante clave pública. El par de claves se generan y
almacenan externamente al código.

## Configuración

- Cambiar la contraseña después de iniciar sesión.
- Recuperar contraseña mediante generación aleatoria y envío por correo.

## Multi Idioma

La app muestra todos sus textos en castellano, euskera o inglés. El usuario puede cambiar el idioma
desde la configuración.

### Instalación

1. Clonar el repositorio
   git clone https://github.com/Ceptorsh0ck/Reto2_app_android.git
2. Abrir el proyecto en Android Studio
3. Ejecutar el proyecto en un emulador o dispositivo físico.

## Inicio Rápido

- Inicia sesión con tus credenciales.
- Si es la primera vez que inicias sesión, se te pedirá cambiar tu contraseña.
- Completa los formularios de confirmación de datos personales.
- Una vez completados los pasos anteriores, podrás acceder a la aplicación con normalidad.

### Prerrequisitos

- Tener instaladas las tecnologías necesarias (Socket.io, Retrofit, Firebase, Room).


### Contacto
- Gorka Gabiña - gorka.gabiname@elorrieta-errekamari.com
- Jon Gallego - jon.gallegoaz@elorrieta-errekamari.com
- Milena Cuellar - milena.cuellarpa@elorrieta-errekamari.com
- Aimar Pelea - aimar.peleaar@elorrieta-errekamari.com

## Licencia
- Este proyecto está bajo la Licencia MIT





