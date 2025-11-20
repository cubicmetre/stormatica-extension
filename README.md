# Stormatica

A fork of [Litematica](https://github.com/maruohon/litematica) with additional features for material lists and coordinating item gathering with player activities.

## What is Stormatica?
Stormatica extends Litematica's material list functionality with two major features:

### Custom Material Lists
Load material lists from JSON or text files containing **any Item Class** - not just placeable blocks:

### Cache-Based Sorting
Material lists automatically prioritize items you've recently accessed in containers:
- Open a chest → Items from that chest jump to the top of your list
- Open more containers → Most recently seen items stay at the top
- Your HUD always shows the items that are most recently accessible to you

## Installation

### Requirements
- Minecraft 1.21.10 (Fabric)
- [Fabric Loader](https://fabricmc.net/) 0.17.3+
- [Malilib](https://www.curseforge.com/minecraft/mc-mods/malilib) 0.26.4+

### Steps
1. Download the latest release from [Releases](https://github.com/cubicmetre/stormatica-extension/releases)
2. Place `stormatica-fabric-0.24.5-stormatica-1.0.0.jar` in your `.minecraft/mods/` folder
3. Disable the original Litematica mod (Stormatica replaces it completely)
4. Launch Minecraft

**Note:** Stormatica is a **fork**, not an addon. Do not run it alongside the original Litematica.

## Usage

### Creating Custom Material Lists

#### JSON Format
```json
{
  "name": "Tools and Utilities",
  "items": [
    {"id": "lead","count": 1},
    {"id": "saddle","count": 1},
    {"id": "spyglass","count": 1},
    {"id": "shears","count": 1},
    {"id": "diamond_axe","count": 1},
    {"id": "diamond_shovel","count": 1},
    {"id": "diamond_hoe","count": 1},
    {"id": "diamond_pickaxe","count": 1}
  ]
}
```

#### Text Format
```
minecraft:diamond_sword 1
minecraft:diamond_chestplate 1
minecraft:golden_apple 10
minecraft:ender_pearl 16
minecraft:shield 1
```

Save files in your schematics folder, then load via "Load Schematics" menu.

### Using Cache-Based Sorting

**Cache sorting is enabled by default!**

1. Load any material list
2. Start opening containers (chests, barrels, shulker boxes, etc.)
3. Items you just saw automatically move to the top
4. Your HUD now shows the most accessible items first

**To change sort order:** Click the **Item** column header to cycle through NAME → CACHE_ORDER → CACHE_ORDER reversed → NAME reversed

## Credits

### Original Litematica
- **Author:** [masa](https://github.com/maruohon)
- **License:** LGPLv3
- **Repository:** https://github.com/maruohon/litematica

### Stormatica Extensions
- **Author:** cubicmetre
- **Features Added:** Custom material lists, cache-based sorting
- **License:** LGPLv3 (same as original)

## Compiling

```bash
git clone https://github.com/cubicmetre/stormatica-extension.git
cd stormatica-extension
./gradlew build
```

The built jar file will be in `build/libs/`

## License

This project is licensed under the GNU Lesser General Public License v3.0 (LGPLv3), the same license as the original Litematica.

## Support

- **Issues:** [GitHub Issues](https://github.com/cubicmetre/stormatica-extension/issues)
- **Original Litematica:** [masa's repository](https://github.com/maruohon/litematica)

This is an **unofficial fork** - not supported by the original Litematica team.
