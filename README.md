# Minecraft-SkFawePlace

The Skript addon plugin.
Add 'fawe_place' effect.

# Dependencies

1. FastAsyncWorldEdit: https://github.com/IntellectualSites/FastAsyncWorldEdit
1. Skript: https://github.com/SkriptLang/Skript

# Usage

## Pattern

1. Default format
```
fawe_place %string% %location% [rotate by %-number%]"
```

2. Specific format
```
fawe_place %string% as %string% %location% [rotate by %-number%]"
```
Second %string% mean schematic file format from FastAsyncWorldEdit.
Use first pattern if you don't know what enter at there.

## Example

```
fawe_place "wools" location at 100, 140, 100 in world "world_nether" rotate by 90
```
