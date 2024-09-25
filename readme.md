# OpenHUD

This mod introduces customizable waypoints, displayed on an in-game HUD and compass.
Waypoints can be created and edited by the player or programmatically on the server side.


## Features

- In game compass HUD displaying waypoints
- Waypoints can have colors or icons
- Waypoints can be created and edited by players in-game as well as programmatically
- Waypoints are stored on the server
- Waypoints can be added, edited, removed or made read-only using the API
- Waypoints are displayed on an in game HUD
- Waypoints can be hidden
- Fully open-source


## API

``` java
ServerPlayer player = ...;

OpenHud.getWaypointManager(player.serverLevel())
        .getWaypoints(player)
        .newWaypoint()
        .name(Component.literal("Programmatic waypoint"))
        .position(new BlockPos(123, 64, 456))
        .color(FastColor.ARGB32.color(255, 0, 0))
        .visible(true)
        .readOnly(true)
        .save();
```