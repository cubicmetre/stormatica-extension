# Cache Sorting - Quick Guide

## What is it?

Cache sorting automatically prioritizes items you've recently seen in containers, making material gathering much more efficient.

## How to use it

1. **Load a material list** (it starts with cache sorting enabled by default)
2. **Open containers** (chests, barrels, shulker boxes, etc.)
3. **Watch the HUD update** - Items you just saw jump to the top of the list
4. **Gather materials** efficiently without running all over your world

## Switching sort modes

Click the **Item** column header in the material list GUI to cycle through:
- **NAME** (alphabetical A-Z)
- **CACHE_ORDER** (recently accessed first) ← The new feature!
- **CACHE_ORDER reversed** (least recent first)
- **NAME reversed** (Z-A)

Click other column headers to sort by count instead.

## Example

**Before cache sorting:**
- Material list shows items alphabetically
- HUD displays: Diamonds, Gold, Iron, Oak Planks, Redstone
- Most items aren't nearby, requiring long travel

**With cache sorting:**
- You open your wood storage
- Oak Planks jump to the top
- You open your ore storage
- Iron and Gold jump to the top
- **HUD now shows only items you just saw and can access immediately!**

## Tips

- Cache persists across container opens during your session
- The more containers you open, the smarter the ordering becomes
- Items not yet found appear at the bottom
- Cache resets when you restart the game

---

For full details, see [ITEM_CACHE_SORTING.md](ITEM_CACHE_SORTING.md)
