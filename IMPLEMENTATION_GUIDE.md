# Item Cache Sorting - Implementation Guide

This document provides step-by-step instructions for implementing the item cache sorting feature in Litematica.

## Overview

The feature adds a new sort mode that prioritizes items based on what the player has recently seen in containers. This solves the problem of material lists showing unrelated items scattered across the world, making survival material gathering much more efficient.

## Implementation Steps

### 1. Create MaterialListItemCache.java

**Location:** `src/main/java/fi/dy/masa/litematica/materials/MaterialListItemCache.java`

**Purpose:** Singleton class that maintains a cache of recently accessed items using LRU (Least Recently Used) ordering.

**Key Components:**
- Uses `LinkedHashSet<ItemType>` to maintain insertion order while preventing duplicates
- `scanContainer(List<Slot> slots)` - Scans a container and updates the cache
- `getCachePriority(ItemStack stack)` - Returns priority index (0 = most recent, Integer.MAX_VALUE = not cached)
- `clear()` - Clears all cached items
- `setEnabled(boolean)` / `isEnabled()` - Enable/disable the cache system
- `getCacheSize()` - Returns number of cached items

**Algorithm Details:**
```java
// When scanning a container:
1. Iterate through all slots
2. For each slot with an item, create ItemType (without NBT)
3. Collect unique items from the container
4. For each unique item:
   - Remove it from the cache (if present)
   - Add it back to the end (moves to "most recent" position)

// When getting priority:
1. Convert ItemStack to ItemType
2. Search through cache from end to beginning
3. Return (cacheSize - 1 - index) to get priority
4. Items at the end have lowest priority numbers (0 = most recent)
```

**Full Implementation:** See the file I created at the path above.

---

### 2. Add CACHE_ORDER to MaterialListBase.java

**Location:** `src/main/java/fi/dy/masa/litematica/materials/MaterialListBase.java`

**Change 1:** Add CACHE_ORDER to the SortCriteria enum (line ~257)

```java
public enum SortCriteria
{
    NAME,
    COUNT_TOTAL,
    COUNT_MISSING,
    COUNT_AVAILABLE,
    CACHE_ORDER;  // <-- ADD THIS

    public static SortCriteria fromStringStatic(String name)
    {
        for (SortCriteria mode : SortCriteria.values())
        {
            if (mode.name().equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return SortCriteria.COUNT_TOTAL;
    }
}
```

**Change 2:** Set CACHE_ORDER as the default sort criteria (line ~25)

```java
protected SortCriteria sortCriteria = SortCriteria.CACHE_ORDER;  // <-- CHANGE from COUNT_TOTAL
```

---

### 3. Update MaterialListSorter.java

**Location:** `src/main/java/fi/dy/masa/litematica/materials/MaterialListSorter.java`

**Change:** Add CACHE_ORDER comparison logic in the `compare()` method (after COUNT_AVAILABLE case, before the final return)

```java
else if (sortCriteria == SortCriteria.CACHE_ORDER)
{
    // Sort by cache priority (lower = more recently accessed)
    MaterialListItemCache cache = MaterialListItemCache.getInstance();
    int priority1 = cache.getCachePriority(entry1.getStack());
    int priority2 = cache.getCachePriority(entry2.getStack());

    if (priority1 == priority2)
    {
        return nameCompare;
    }

    return (priority1 < priority2) != reverse ? -1 : 1;
}
```

**Logic Explanation:**
- Lower priority numbers = more recently accessed
- Priority 0 = most recently seen item
- Priority Integer.MAX_VALUE = not in cache (appears at bottom)
- Falls back to alphabetical (nameCompare) when priorities are equal
- Respects the reverse flag for reversed sorting

---

### 4. Hook Container Scanning in MixinHandledScreen.java

**Location:** `src/main/java/fi/dy/masa/litematica/mixin/screen/MixinHandledScreen.java`

**Change 1:** Add imports
```java
import fi.dy.masa.litematica.materials.MaterialListItemCache;
```

**Change 2:** Add instance variable to track scanning state
```java
@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends Screen
{
    private boolean litematica_containerScanned = false;  // <-- ADD THIS

    private MixinHandledScreen(Text title)
    {
        super(title);
    }
```

**Change 3:** Modify the `litematica_renderSlotHighlightsPre` method to scan containers

```java
@Inject(method = "renderMain", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
private void litematica_renderSlotHighlightsPre(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci)
{
    HandledScreen<?> screen = (HandledScreen<?>) (Object) this;

    // Scan container items for material list cache (only once per screen open)
    if (!this.litematica_containerScanned)
    {
        MaterialListItemCache.getInstance().scanContainer(screen.getScreenHandler().slots);
        this.litematica_containerScanned = true;
    }

    MaterialListHudRenderer.renderLookedAtBlockInInventory(drawContext, screen, this.client);
}
```

**Change 4:** Add close handler to reset the scan flag

```java
@Inject(method = "close", at = @At("HEAD"))
private void litematica_onContainerClose(CallbackInfo ci)
{
    // Reset the scanned flag when container closes
    this.litematica_containerScanned = false;
}
```

**Why this works:**
- `renderMain` is called when the container screen renders
- We scan only once per screen open using the boolean flag
- The flag resets when the container closes
- `screen.getScreenHandler().slots` gives us all slots in the container
- This includes player inventory AND the opened container

---

### 5. Update WidgetMaterialListEntry.java for Column Cycling

**Location:** `src/main/java/fi/dy/masa/litematica/gui/widgets/WidgetMaterialListEntry.java`

**Change:** Modify the `onMouseClickedImpl` method's column 0 case to cycle between NAME and CACHE_ORDER

**Replace this section (lines ~156-172):**
```java
switch (column)
{
    case 0:
        this.materialList.setSortCriteria(SortCriteria.NAME);
        break;
    // ... rest of cases
}
```

**With this:**
```java
switch (column)
{
    case 0:
        // Column 0 cycles between NAME and CACHE_ORDER
        SortCriteria currentCriteria = this.materialList.getSortCriteria();
        if (currentCriteria == SortCriteria.NAME)
        {
            this.materialList.setSortCriteria(SortCriteria.CACHE_ORDER);
        }
        else if (currentCriteria == SortCriteria.CACHE_ORDER)
        {
            // If already on CACHE_ORDER, toggle reverse (or go back to NAME if reversed)
            if (!this.materialList.getSortInReverse())
            {
                this.materialList.setSortCriteria(SortCriteria.CACHE_ORDER); // toggles reverse
            }
            else
            {
                this.materialList.setSortCriteria(SortCriteria.NAME);
            }
        }
        else
        {
            // From any other sort, clicking column 0 goes to NAME
            this.materialList.setSortCriteria(SortCriteria.NAME);
        }
        break;
    case 1:
        this.materialList.setSortCriteria(SortCriteria.COUNT_TOTAL);
        break;
    case 2:
        this.materialList.setSortCriteria(SortCriteria.COUNT_MISSING);
        break;
    case 3:
        this.materialList.setSortCriteria(SortCriteria.COUNT_AVAILABLE);
        break;
    default:
        return false;
}
```

**Cycle Pattern:**
1. NAME → CACHE_ORDER
2. CACHE_ORDER → CACHE_ORDER (reversed)
3. CACHE_ORDER (reversed) → NAME
4. NAME → NAME (reversed) [existing behavior from setSortCriteria]

---

## How It Works (User Perspective)

### Workflow
1. Player loads a material list (defaults to CACHE_ORDER sorting)
2. Player opens a chest
3. `MixinHandledScreen` detects the screen opening
4. All items in the chest are scanned and added to the cache
5. Material list automatically re-sorts on next update (every 2 seconds)
6. HUD shows cached items first
7. Player opens more containers → more items get cached and prioritized
8. Player can manually switch sort modes by clicking column headers

### Example
**Material List Contains:**
- 64 Oak Planks
- 32 Diamonds
- 16 Iron Ingots
- 8 Gold Ingots
- 4 Redstone Dust

**Player Actions:**
1. Opens wood storage → Oak Planks cached → Oak Planks appears at top of HUD
2. Opens ore storage → Diamonds, Iron, Gold cached → These jump to top
3. HUD now shows: Iron, Gold, Diamonds, Oak Planks, Redstone (in order of most recently accessed)

---

## Technical Details

### Why LinkedHashSet?
- Maintains insertion order (critical for LRU)
- Prevents duplicates (each item appears once)
- O(1) add/remove operations
- Iteration order is predictable (insertion order)

### Why scan in renderMain?
- Called reliably when container screen is shown
- Has access to ScreenHandler which contains all slots
- Runs before first render so items are cached immediately
- No need to hook into complex container opening events

### Cache Priority Algorithm
```
Cache contents: [Apple, Bread, Carrot, Diamond, Emerald]
                  (oldest)                    (newest)

Priorities:
- Emerald: 0 (most recent, highest priority)
- Diamond: 1
- Carrot: 2
- Bread: 3
- Apple: 4
- Stone (not cached): Integer.MAX_VALUE (lowest priority)

Sorting order when using CACHE_ORDER:
1. Emerald (priority 0)
2. Diamond (priority 1)
3. Carrot (priority 2)
4. Bread (priority 3)
5. Apple (priority 4)
6. Stone (priority MAX_VALUE)
```

### Performance Considerations
- Container scanning: O(n) where n = number of slots
- Priority lookup: O(m) where m = cache size
- Typical cache size: 50-200 items (small)
- Typical material list: 10-100 items (small)
- Overall performance impact: Negligible

### Memory Usage
- Each ItemType in cache: ~48 bytes (Java object overhead + ItemType data)
- 200 cached items: ~9.6 KB
- Completely negligible for modern systems

---

## Testing Checklist

### Basic Functionality
- [ ] Material list defaults to CACHE_ORDER sorting on creation
- [ ] Opening a chest caches all items from that chest
- [ ] Cached items appear at top of material list
- [ ] Most recently opened container's items appear first
- [ ] Items not in any opened container appear at bottom

### Column Cycling
- [ ] Clicking "Item" column cycles: NAME → CACHE_ORDER → CACHE_ORDER (rev) → NAME → ...
- [ ] Clicking "Total" column sorts by total count
- [ ] Clicking "Missing" column sorts by missing count
- [ ] Clicking "Available" column sorts by available count

### Edge Cases
- [ ] Empty containers don't cause crashes
- [ ] Containers with duplicate items only cache once
- [ ] Player inventory items are also cached (it's part of the slots)
- [ ] Shulker boxes in containers are handled correctly
- [ ] Opening the same container multiple times updates cache position

### Persistence
- [ ] Cache persists when closing/opening containers
- [ ] Cache persists when switching between different material lists
- [ ] Cache can be manually cleared (if clear() is exposed via config)
- [ ] Cache resets properly when disabled

---

## Optional Enhancements

### Config Options
Add to Litematica config:
```java
public static final ConfigBoolean MATERIAL_LIST_CACHE_ENABLED = new ConfigBoolean("materialListCacheEnabled", true);
public static final ConfigInteger MATERIAL_LIST_CACHE_MAX_SIZE = new ConfigInteger("materialListCacheMaxSize", 1000, 0, 10000);
```

Then in MaterialListItemCache:
```java
public void scanContainer(List<Slot> slots)
{
    if (!Configs.Generic.MATERIAL_LIST_CACHE_ENABLED.getBooleanValue())
    {
        return;
    }

    // ... existing code

    // Limit cache size
    int maxSize = Configs.Generic.MATERIAL_LIST_CACHE_MAX_SIZE.getIntegerValue();
    while (this.cachedItems.size() > maxSize)
    {
        // Remove oldest item (first in set)
        Iterator<ItemType> it = this.cachedItems.iterator();
        if (it.hasNext())
        {
            it.next();
            it.remove();
        }
    }
}
```

### Visual Indicators
Add to MaterialListHudRenderer to show which items are cached:
```java
// In render method, when drawing items:
MaterialListItemCache cache = MaterialListItemCache.getInstance();
for (int i = 0; i < size; ++i)
{
    MaterialListEntry entry = list.get(i);
    int priority = cache.getCachePriority(entry.getStack());

    // Draw item normally
    drawContext.drawItem(entry.getStack(), x, y);

    // Draw indicator for cached items
    if (priority < Integer.MAX_VALUE)
    {
        // Draw small colored border or icon
        RenderUtils.drawOutline(drawContext, x, y, 16, 16, 1, 0xFF00FF00);
    }

    y += lineHeight;
}
```

### Cache Persistence
Save cache to JSON file:
```java
public JsonObject toJson()
{
    JsonObject obj = new JsonObject();
    JsonArray items = new JsonArray();

    for (ItemType itemType : this.cachedItems)
    {
        items.add(itemType.getStack().getItem().toString());
    }

    obj.add("cached_items", items);
    obj.add("timestamp", new JsonPrimitive(System.currentTimeMillis()));

    return obj;
}

public void fromJson(JsonObject obj)
{
    // Load and restore cache
    // Consider timestamp to auto-clear old caches
}
```

---

## Files Summary

### New Files
1. **MaterialListItemCache.java** - Core cache implementation (see created file)

### Modified Files
1. **MaterialListBase.java** (2 changes)
   - Add CACHE_ORDER to enum
   - Change default sortCriteria

2. **MaterialListSorter.java** (1 change)
   - Add CACHE_ORDER comparison case

3. **MixinHandledScreen.java** (3 changes)
   - Add import
   - Add boolean field
   - Modify renderSlotHighlightsPre
   - Add close injection

4. **WidgetMaterialListEntry.java** (1 change)
   - Update column 0 click handler

---

## Build Verification

After implementing:
```bash
./gradlew build
```

Expected output:
```
BUILD SUCCESSFUL in XXs
```

If there are compilation errors, check:
1. All imports are correct
2. MaterialListItemCache.java is in the correct package
3. Mixin injection points match your Minecraft version
4. No typos in method names or field references

---

## Additional Notes

### Why This Approach?

**Alternative Considered:** Add a "recently accessed" timestamp to each MaterialListEntry
- **Problem:** MaterialListEntry is created from schematic data, not runtime
- **Problem:** Would require modifying core material list creation logic
- **Problem:** Doesn't persist across material list recreations

**Chosen Approach:** Separate cache singleton
- **Benefit:** Clean separation of concerns
- **Benefit:** Works with any material list (schematic, placement, custom)
- **Benefit:** Easy to enable/disable
- **Benefit:** No changes to data structures

### Thread Safety
The current implementation is **not thread-safe** because:
- All access happens on the client render thread
- Minecraft client is single-threaded for UI/rendering
- Container scanning happens during render phase

If thread safety is needed:
```java
private final Set<ItemType> cachedItems = Collections.synchronizedSet(new LinkedHashSet<>());
```

### Compatibility
- Works with Fabric and likely adaptable to Forge with mixin changes
- Compatible with existing material list features
- Doesn't break save file compatibility
- Doesn't modify network packets
- Client-side only feature

---

## Support and Troubleshooting

### Common Issues

**Items not being cached:**
- Check that MaterialListItemCache.getInstance().isEnabled() returns true
- Verify MixinHandledScreen injection is working (add debug log)
- Confirm container has slots with items

**Sorting not working:**
- Verify CACHE_ORDER enum is properly added
- Check MaterialListSorter has the new case
- Confirm material list sortCriteria is set to CACHE_ORDER

**Build failures:**
- Missing imports (add MaterialListItemCache import)
- Wrong mixin target (check Minecraft version compatibility)
- Package structure (ensure MaterialListItemCache is in materials package)

### Debug Logging

Add to MaterialListItemCache.scanContainer():
```java
public void scanContainer(List<Slot> slots)
{
    if (!this.enabled)
    {
        Litematica.logger.info("Cache disabled, skipping scan");
        return;
    }

    List<ItemType> foundItems = new ArrayList<>();
    // ... scanning logic

    Litematica.logger.info("Scanned container: found {} unique items, cache now has {} items",
                          foundItems.size(), this.cachedItems.size());
}
```

Add to MaterialListSorter.compare():
```java
else if (sortCriteria == SortCriteria.CACHE_ORDER)
{
    MaterialListItemCache cache = MaterialListItemCache.getInstance();
    int priority1 = cache.getCachePriority(entry1.getStack());
    int priority2 = cache.getCachePriority(entry2.getStack());

    Litematica.logger.debug("Comparing {} (priority {}) vs {} (priority {})",
                           entry1.getStack().getName().getString(), priority1,
                           entry2.getStack().getName().getString(), priority2);
    // ... comparison logic
}
```

---

## Contact

If the Litematica maintainer has questions about this implementation, they can:
1. Review the created MaterialListItemCache.java file for the complete implementation
2. Check ITEM_CACHE_SORTING.md for user-facing documentation
3. Review this guide for technical details

The implementation is designed to be:
- **Minimal** - Only 5 file changes
- **Clean** - Separate concerns, no data structure modifications
- **Safe** - No save file changes, client-side only
- **Maintainable** - Clear logic, well-commented
- **Optional** - Easy to disable via config if needed
