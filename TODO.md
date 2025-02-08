# TODO
Writing these down here so I don't forget anything

- general cleanup
## Fixes

## QOL/Improvements
- Improve star power generation:
  - account for large breaks
  - account for long sustains
  - account for song end (stop putting phrases 4-6 bars before the last note) (this is technically done but it's only in the bad sp code)
## Additions
- Support BPM changes and non-4/4-sections  
Currently, the way the code works, this would require iterating over all sections in multiple places;  
There is probably a better way of doing that, though it would take some time;
General cleanup would probably be a good idea before adding more things.
- GUI for controlling the program
- Generate all fnf difficulties if available  
Maybe make an option for which difficulty to change?  
If not (or: as the default value): Which difficulty to leave empty?
  - Easy because it should be 3-fret but fnf is 4-fret on all diffs
  - Hard to leave space for custom underchart of the Expert chart
  - Expert to leave space for accurate chart  
  - Medium because ??? idk
- Toggleable options for generation process:
  - Split generation (as this already exists and splits the charts to coop and rythm guitar, does it even need a toggle?)
  - GRYB instead of RYBO
  - reading song metadata from supplied song.ini

