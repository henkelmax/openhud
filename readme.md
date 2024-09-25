# OpenHUD

This mod introduces customizable waypoints, displayed on an in-game HUD.
Waypoints can be created and edited by the player or programmatically on the server side.

![](https://github.com/user-attachments/assets/a771bc9d-3f3f-464d-81f4-38743e0de0aa)

![](https://github.com/user-attachments/assets/7715347f-64e1-4816-906f-ff5b18d34b44)

![](https://github.com/user-attachments/assets/0b9b1e8d-7702-4dba-93d5-976412d9df11)

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

## Planned Features

- More default icons
- Waypoint icon selection GUI
- Client-side API
- Sharing waypoints
- Commands
- Better settings screen
