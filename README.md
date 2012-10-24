Vert.x Scala Clock
==================

This application uses Vert.x + SockJS to show a web page that displays a constantly incrementing counter.
The Timer runs within Vert.x and pushes events to the browser using SocketJS.

You can build and deploy on Cloud Foundry using the following command:

	gradle assembleApp cf-push -PcfUser=<YourUserId> -PcfPasswd=<YourPassword>

Please note
* Vert.x requires Java 7
