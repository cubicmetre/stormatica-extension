# Item Cache Sorting for Material Lists

## Overview

The Item Cache sorting feature improves the material list workflow in survival mode by automatically prioritizing items you have recently accessed in containers. This solves the problem of the HUD showing unrelated items scattered across the world, making it much easier to gather materials efficiently.

## How It Works

### Automatic Container Scanning

Whenever you open a container (chest, barrel, shulker box, etc.), Litematica automatically:

1. Scans all unique item types in that container
2. Updates an internal cache with these items
3. Moves items to the front of the cache (most recently accessed = highest priority)
4. Reorders the material list to show cached items first

### LRU (Least Recently Used) Ordering

The cache uses a "most recent first" strategy:

- Items from the most recently opened container appear at the top
- Items seen in earlier containers gradually move down the list
- Items not yet found in any container appear at the bottom

### Benefits

**Before:** The material list shows items alphabetically or by count, resulting in the HUD displaying unrelated items requiring travel to disparate locations.

**After:** The material list prioritizes items you've recently seen, so the HUD always shows items that are currently accessible to you.

### Example Scenario

Suppose your material list needs:
- 64 Oak Planks
- 32 Diamonds
- 16 Iron Ingots
- 8 Gold Ingots
- 4 Redstone Dust

**Traditional alphabetical sorting** might show:
1. Diamonds (not available nearby)
2. Gold Ingots (not available nearby)
3. Iron Ingots (not available nearby)
4. Oak Planks (available)
5. Redstone Dust (not available nearby)

**With cache sorting**, after opening your wood storage:
1. Oak Planks ← Just seen in storage!
2. Diamonds
3. Gold Ingots
4. Iron Ingots
5. Redstone Dust

Then you open your ore storage containing Iron and Gold:
1. Iron Ingots ← Just seen!
2. Gold Ingots ← Just seen!
3. Oak Planks ← Previously cached
4. Diamonds
5. Redstone Dust

## Default Behavior

**Cache sorting is enabled by default** for all material lists. When you load or create a material list, it will automatically use `CACHE_ORDER` sorting.

## Changing Sort Order

You can manually change the sort order by clicking on column headers in the material list GUI:

### Item Column (Column 0)
Clicking the **Item** column header cycles through:
1. **NAME** → Alphabetical sorting (A-Z)
2. **CACHE_ORDER** → Recently accessed items first
3. **CACHE_ORDER (reversed)** → Least recently accessed first
4. **NAME (reversed)** → Alphabetical sorting (Z-A)
5. (cycles back to NAME)

### Other Columns
- **Total** column → Sort by total count needed (COUNT_TOTAL)
- **Missing** column → Sort by missing count (COUNT_MISSING)
- **Available** column → Sort by available count (COUNT_AVAILABLE)

Clicking the same column header again reverses the sort order for that column.

**To return to cache sorting:** Simply click the **Item** column header until it shows cache-ordered items (recently accessed items at the top).

## Technical Details

### Implementation

- **MaterialListItemCache**: Singleton class managing the cache
- **LinkedHashSet**: Maintains insertion order while preventing duplicates
- **MixinHandledScreen**: Hooks into container rendering to scan items
- **MaterialListSorter**: Includes CACHE_ORDER comparison logic
- **MaterialListBase.SortCriteria**: New CACHE_ORDER enum value

### Performance

- Container scanning happens once when the container is first opened
- Cache lookups use O(n) search on a LinkedHashSet (very fast for typical material lists)
- No performance impact when not using cache sorting
- Cache persists across container opens/closes during the session

### Cache Management

The cache:
- Persists for the entire game session
- Clears when the cache is disabled
- Can be manually cleared (future config option)
- Does not save between game sessions

## Future Enhancements

Potential improvements:
- Config option to enable/disable cache sorting
- Manual cache clear button
- Cache persistence across sessions
- Config to limit cache size
- Visual indicator in HUD showing cached vs non-cached items
