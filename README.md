# Minecraft-SkFawePlace

The Skript addon plugin.
Add 'fawe_place' effect.

# Dependencies

1. FastAsyncWorldEdit: https://github.com/IntellectualSites/FastAsyncWorldEdit
1. Skript: https://github.com/SkriptLang/Skript

# Usage

## Pattern

```
1. fawe_place %string% [as %-string%] %location% [rotate by %-number%]
2. fawe_place %string% [as %-string%] %location% [rotate by %-number%] with entity
```

'as %-string%' mean schematic file format from FastAsyncWorldEdit.
Skip it, if you don't know what should enter at there.

with entity mean paste entities, if give option -e when you saved schematic file


## Example

1. Place 'wool' schematic file at x:100, y:140, z:100 in The Nether
```
fawe_place "wools" location at 100, 140, 100 in world "world_nether"
```

1. Place 'poles.nbt' file at x:100, y:140, z:100 in Overworld
```
fawe_place "poles" as "structure" location at 100, 140, 100 rotate by 90
```
