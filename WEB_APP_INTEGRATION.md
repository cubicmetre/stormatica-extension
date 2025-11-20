# Web App Integration Guide

This document describes how to integrate your web-based item sorter with Litematica's custom material list feature.

## Format Specification

Use the following JSON format for seamless integration between your web app and Litematica:

```json
{
  "name": "Cubic Sorter Layout",
  "items": [
    {"id": "minecraft:stone", "count": 64, "cell": 0},
    {"id": "minecraft:stone_stairs", "count": 32, "cell": 0},
    {"id": "minecraft:dirt", "count": 128, "cell": 1},
    {"id": "minecraft:grass_block", "count": 64, "cell": 1}
  ]
}
```

### Field Descriptions

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | No | Display name for the material list (defaults to filename) |
| `items` | array | **Yes** | Array of item objects |
| `items[].id` | string | **Yes** | Minecraft item ID with namespace (e.g., `minecraft:stone`) |
| `items[].count` | integer | **Yes** | Quantity of the item |
| `items[].cell` | integer | No | Cell/group ID for layout reconstruction (**ignored by Litematica**) |

### Key Points

✅ **Litematica reads:** `id` and `count`
✅ **Litematica ignores:** `cell` (and any other unknown fields)
✅ **Your web app uses:** `cell` to group items into grid positions

## Workflow

### 1. Web App → Litematica (Player gathers items)

```
Your Web App
  ├─ User designs item layout in grid
  ├─ Export to JSON with cell groupings
  └─ Save to .minecraft/schematics/

Player in Minecraft
  ├─ Press M (Litematica menu)
  ├─ Load Schematics → Browse to your.json
  ├─ Click "Material List" button
  ├─ Enable "Info HUD: ON"
  └─ HUD shows items needed + inventory tracking
```

### 2. Litematica → Web App (Import schematic materials)

```
Player in Minecraft
  ├─ Load a .litematic schematic
  ├─ Open Material List
  ├─ Click "Export JSON" button
  └─ File saved to .minecraft/schematics/

Your Web App
  ├─ Import the exported JSON
  ├─ Items have no `cell` field
  ├─ User manually assigns items to cells
  └─ Save with cell data for future use
```

## Cell Grouping System

The `cell` field is an **integer** that groups items together. Items with the same `cell` value belong to the same grid position/column/group in your layout.

**Example: 3x3 Grid**

```
Grid Layout (visual):
┌─────────┬─────────┬─────────┐
│  Cell 0 │  Cell 1 │  Cell 2 │
│  Stone  │  Dirt   │  Wood   │
│  Cobble │  Grass  │  Planks │
└─────────┴─────────┴─────────┘

JSON Format:
{
  "name": "3x3 Grid",
  "items": [
    {"id": "minecraft:stone", "count": 64, "cell": 0},
    {"id": "minecraft:cobblestone", "count": 64, "cell": 0},
    {"id": "minecraft:dirt", "count": 64, "cell": 1},
    {"id": "minecraft:grass_block", "count": 64, "cell": 1},
    {"id": "minecraft:oak_log", "count": 64, "cell": 2},
    {"id": "minecraft:oak_planks", "count": 64, "cell": 2}
  ]
}
```

## Converting Grid Format to Litematica Format

If you have existing grid-based JSON files (like `cubic1.json`, `cubic2.json`), use the provided converter:

```bash
python convert_grid_to_litematica.py cubic1.json cubic1_converted.json "Cubic Sorter 1"
```

**Input Format (grid):**
```json
[
  ["stone", "stone_stairs", "dirt", null],
  ["cobblestone", "grass_block", null, null]
]
```

**Output Format (Litematica-compatible):**
```json
{
  "name": "Cubic Sorter 1",
  "items": [
    {"id": "minecraft:stone", "count": 1, "cell": 0},
    {"id": "minecraft:stone_stairs", "count": 1, "cell": 0},
    {"id": "minecraft:dirt", "count": 1, "cell": 0},
    {"id": "minecraft:cobblestone", "count": 1, "cell": 1},
    {"id": "minecraft:grass_block", "count": 1, "cell": 1}
  ]
}
```

## Web App Implementation Tips

### Exporting from Web App

```javascript
function exportToLitematica(layout, name) {
  const items = [];

  layout.cells.forEach((cell, cellIndex) => {
    cell.items.forEach(item => {
      items.push({
        id: `minecraft:${item.id}`,  // Add namespace
        count: item.quantity,
        cell: cellIndex
      });
    });
  });

  const json = {
    name: name,
    items: items
  };

  // Download as .json file
  downloadJSON(json, `${name}.json`);
}
```

### Importing from Litematica

```javascript
function importFromLitematica(jsonFile) {
  const data = JSON.parse(jsonFile);

  // Group items by cell (if present)
  const cellGroups = {};

  data.items.forEach(item => {
    const cellId = item.cell ?? 'unassigned';  // Default if no cell

    if (!cellGroups[cellId]) {
      cellGroups[cellId] = [];
    }

    cellGroups[cellId].push({
      id: item.id.replace('minecraft:', ''),  // Remove namespace
      quantity: item.count
    });
  });

  return {
    name: data.name,
    cells: cellGroups
  };
}
```

### Reconstructing Grid from Cells

```javascript
function rebuildGrid(cellGroups, gridWidth, gridHeight) {
  const grid = Array(gridHeight).fill(null).map(() => Array(gridWidth).fill(null));

  Object.entries(cellGroups).forEach(([cellId, items]) => {
    const x = cellId % gridWidth;
    const y = Math.floor(cellId / gridWidth);

    if (y < gridHeight && x < gridWidth) {
      grid[y][x] = items;
    }
  });

  return grid;
}
```

## File Location

Save your JSON files to the Litematica schematics directory:

**Default:**
```
.minecraft/schematics/
```

**Custom (configurable in Litematica settings):**
```
Litematica Configs → Generic → customSchematicBaseDirectory
```

## Example Files

### Minimal Example
```json
{
  "name": "Shopping List",
  "items": [
    {"id": "minecraft:diamond_pickaxe", "count": 3},
    {"id": "minecraft:golden_apple", "count": 16}
  ]
}
```

### With Cell Grouping
```json
{
  "name": "Cubic Sorter - Stone Family",
  "items": [
    {"id": "minecraft:stone", "count": 64, "cell": 0},
    {"id": "minecraft:cobblestone", "count": 128, "cell": 0},
    {"id": "minecraft:stone_bricks", "count": 64, "cell": 0},
    {"id": "minecraft:deepslate", "count": 64, "cell": 1},
    {"id": "minecraft:cobbled_deepslate", "count": 128, "cell": 1}
  ]
}
```

### Large Scale (1369 items, 36 cells)
See `cubic1_converted.json` for a real-world example of a complete cubic chunk sorting system.

## Benefits of This Integration

1. **Bidirectional Workflow** - Design in web app → gather in-game, or export from schematics → organize in web app
2. **Layout Preservation** - `cell` field preserves your grid organization
3. **Simple Format** - Easy to generate and parse
4. **Flexible** - Add custom fields as needed (all unknown fields are ignored by Litematica)
5. **No Conflicts** - Litematica never modifies or re-exports the `cell` field
6. **HUD Support** - Real-time inventory tracking while gathering items

## Testing

1. **Create a test file:**
   ```json
   {
     "name": "Test List",
     "items": [
       {"id": "minecraft:diamond", "count": 10, "cell": 0},
       {"id": "minecraft:emerald", "count": 5, "cell": 1}
     ]
   }
   ```

2. **In Minecraft:**
   - Save to `.minecraft/schematics/test.json`
   - Open Litematica (M key)
   - Load Schematics → test.json
   - Click "Material List"
   - Verify items appear correctly

3. **Verify cell preservation:**
   - Import the same file in your web app
   - Check that items are grouped by `cell` value
   - Modify and re-export
   - Reload in Litematica

## Technical Notes

- **Namespace requirement:** Item IDs must include namespace (e.g., `minecraft:stone`, not just `stone`)
- **Case sensitivity:** Item IDs are case-sensitive
- **Invalid items:** Items with invalid IDs are skipped with a warning in the log
- **Count validation:** Counts must be positive integers
- **File size:** No practical limit (tested with 1369 items successfully)
- **Performance:** Material lists update inventory counts every 2 seconds
- **Container scanning:** Automatically scans shulker boxes and bundles recursively

## Support

For issues or questions:
- **Litematica mod:** https://github.com/maruohon/litematica
- **Custom material lists:** See `CUSTOM_MATERIAL_LISTS.md`

---

**Last Updated:** 2025-01-18
**Compatible with:** Litematica 1.21.10-0.24.5+
