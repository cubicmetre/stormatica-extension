# Custom Material Lists for Litematica

This feature extends Litematica's material list functionality to support **custom item lists** that can include any Minecraft item, not just placeable blocks.

## Overview

The standard Litematica material list is generated from schematic blocks and can only display materials for blocks that can be placed in the world. This extension allows you to create custom material lists that include:

- **Tools** (pickaxes, swords, axes, etc.)
- **Food** (cooked beef, golden apples, etc.)
- **Utility items** (ender pearls, buckets, etc.)
- **Non-placeable items** (any item in the game)

These custom lists use the same HUD overlay and inventory tracking features as regular material lists, making them perfect for resource gathering, shopping lists, or planning expeditions.

## Features

✅ **Load custom item lists from files** (JSON or TXT format)
✅ **Export any material list to JSON** for reuse
✅ **Full HUD overlay support** with real-time inventory tracking
✅ **Automatic inventory scanning** (including shulker boxes and bundles)
✅ **Sorting and filtering** like regular material lists
✅ **Simple file formats** - easy to create and edit by hand

## File Formats

### JSON Format

JSON files provide a structured format with a name field:

```json
{
  "name": "My Shopping List",
  "items": [
    {"id": "minecraft:diamond_pickaxe", "count": 5},
    {"id": "minecraft:cooked_beef", "count": 64},
    {"id": "minecraft:ender_pearl", "count": 16}
  ]
}
```

**Fields:**
- `name` (optional): Display name for the material list
- `items` (required): Array of item entries
  - `id` (required): Minecraft item ID (namespace:item format)
  - `count` (required): Quantity needed
  - `cell` (optional): Integer cell/group ID - **ignored by Litematica**, used by external tools to preserve layout groupings

**Extended Format with Cell Grouping:**

For integration with external tools (like web-based item sorters), you can include a `cell` field to group items:

```json
{
  "name": "Cubic Sorter Layout",
  "items": [
    {"id": "minecraft:stone", "count": 64, "cell": 0},
    {"id": "minecraft:stone_stairs", "count": 32, "cell": 0},
    {"id": "minecraft:cobblestone", "count": 128, "cell": 0},
    {"id": "minecraft:dirt", "count": 64, "cell": 1},
    {"id": "minecraft:grass_block", "count": 32, "cell": 1}
  ]
}
```

Items with the same `cell` value belong to the same group/column in your sorting system. Litematica will load all items normally and ignore the `cell` field, while your web app can use it to reconstruct the grid layout.

### Text Format

Text files provide a simple, line-based format:

```txt
# My shopping list
# Lines starting with # are comments

minecraft:diamond_pickaxe 5
minecraft:cooked_beef 64
minecraft:ender_pearl 16
```

**Format:**
- Lines starting with `#` are comments (ignored)
- Empty lines are ignored
- Each item line: `item_id count`
- Item IDs must use the `namespace:item` format

## How to Use

### Loading Custom Material Lists

1. **Create your custom list file**
   - Save it as a `.json` or `.txt` file
   - Place it in your schematics folder (configurable in Litematica settings)

2. **Open the schematic browser**
   - Press `M` (default) to open Litematica menu
   - Click "Load Schematics"

3. **Load the material list**
   - Navigate to your custom list file
   - Click the **"Material List"** button
   - The custom material list will open with full HUD support

### Exporting Material Lists

You can export any existing material list (from schematics, placements, or area analyzers) to a custom JSON format:

1. **Open any material list**
   - From a schematic, placement, or area selection

2. **Click "Export JSON" button**
   - The button is in the material list GUI
   - File will be saved to your schematics directory
   - File name is based on the material list name

3. **The exported file can be reloaded**
   - Use the schematic browser
   - Works just like hand-crafted custom lists

### Using the HUD Overlay

Custom material lists support the full HUD overlay:

1. **Enable HUD in the material list GUI**
   - Click "Info HUD: OFF" to turn it ON

2. **The HUD will display:**
   - Items you still need
   - Quantities required
   - Current inventory counts (updated every 2 seconds)
   - Items in shulker boxes and bundles

3. **Customize HUD settings:**
   - Position, scale, colors via Litematica configs
   - Sorting options (by name, count, available)
   - Filter to show only missing items

## Example Files

### example_custom_material_list.json
```json
{
  "name": "Example Shopping List",
  "items": [
    {"id": "minecraft:diamond_pickaxe", "count": 5},
    {"id": "minecraft:diamond_sword", "count": 3},
    {"id": "minecraft:cooked_beef", "count": 64},
    {"id": "minecraft:ender_pearl", "count": 16},
    {"id": "minecraft:golden_apple", "count": 8},
    {"id": "minecraft:torch", "count": 256},
    {"id": "minecraft:oak_planks", "count": 128}
  ]
}
```

### example_custom_material_list.txt
```txt
# Example custom material list

# Tools
minecraft:diamond_pickaxe 5
minecraft:diamond_sword 3
minecraft:diamond_axe 2

# Food
minecraft:cooked_beef 64
minecraft:golden_apple 8

# Utility items
minecraft:ender_pearl 16
minecraft:torch 256
minecraft:bucket 10

# Building blocks
minecraft:oak_planks 128
minecraft:stone_bricks 256
minecraft:glass 64
```

## Use Cases

### Resource Gathering Lists
Create lists of resources needed for your projects:
```json
{
  "name": "Mega Base Resources",
  "items": [
    {"id": "minecraft:oak_log", "count": 2048},
    {"id": "minecraft:stone", "count": 10000},
    {"id": "minecraft:iron_ingot", "count": 1728}
  ]
}
```

### Adventure/Expedition Prep
Pack lists for expeditions:
```json
{
  "name": "End Raiding Kit",
  "items": [
    {"id": "minecraft:ender_pearl", "count": 32},
    {"id": "minecraft:golden_apple", "count": 16},
    {"id": "minecraft:slow_falling_potion", "count": 4},
    {"id": "minecraft:diamond_pickaxe", "count": 3}
  ]
}
```

### Shopping Lists
Items to acquire from shops or farms:
```txt
# Shopping list for redstone farm
minecraft:redstone 640
minecraft:observer 32
minecraft:sticky_piston 16
minecraft:slime_block 24
```

## Technical Details

### Item Types
- Custom lists use `ItemType` for comparison (ignores NBT data)
- Only the item ID matters - no enchantments, names, etc.
- All items in the registry are supported

### Inventory Tracking
- Scans player inventory every 2 seconds
- Includes items in shulker boxes (recursively)
- Includes items in bundles (recursively)
- Supports nested containers

### Material List Behavior
- `countTotal` = total quantity in the list
- `countMissing` = total quantity (since nothing is "placed")
- `countAvailable` = items found in player inventory
- `countMismatched` = always 0 (not applicable)

### File Location
Custom material list files should be placed in:
- Default: `.minecraft/schematics/`
- Or your custom schematic directory (if configured)

## Troubleshooting

### "Failed to load custom material list"
- Check file format (valid JSON or TXT)
- Verify item IDs are correct (`namespace:item`)
- Ensure counts are positive integers
- Check console logs for detailed error messages

### Items not showing in HUD
- Ensure HUD is enabled (Info HUD: ON)
- Check filter settings (Hide available: OFF to see all)
- Verify items are in the list with `countMissing > 0`

### Wrong inventory counts
- Wait 2 seconds for HUD to refresh
- Check items are in player inventory (not in offhand/armor slots)
- Shulker boxes must have items (empty boxes are skipped)

## Implementation Details

**New Classes:**
- `MaterialListCustom` - Custom material list implementation
- `MaterialListUtils.createMaterialListFromItems()` - Direct item→entry conversion

**Modified Classes:**
- `GuiSchematicLoad` - Added support for loading .json and .txt files
- `GuiMaterialList` - Added "Export JSON" button
- `FileType` - Added TXT type

**File Parsers:**
- JSON parser using Gson (already in Litematica)
- Text parser using BufferedReader
- Auto-detection by file extension

---

## Credits

Custom Material Lists feature extension for Litematica 1.21.10
Extends the original material list system by masa to support arbitrary item types.
