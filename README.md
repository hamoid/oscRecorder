# A simple OSC recorder and player

I use this program to record OSC data coming from Ableton Live.
This data contains real time track volumes, MIDI key presses, multi-touch
finger pressure values and MIDI controller knob values.

I use this data for my live visuals.

To rehearse, I record this data together with an audio file.
Then I play it back multiple times adjusting my visuals without needing to have
the musician play all day :-)

## Recorder

### Start the recorder program

```
./gradlew run -Popenrndr.application=RecorderKt
```

### The program starts recording data when it receives the `/scene_launch` OSC
message.

## Press ESC to end

It will save a text file in the application folder.


## Player

... in progress...
