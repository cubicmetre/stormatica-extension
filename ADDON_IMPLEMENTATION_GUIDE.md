# Litematica Material List Enhancements - Addon Implementation Guide

This guide explains how to implement the custom material lists and item cache sorting features as a **standalone Fabric mod addon** for Litematica using mixins.

## Project Structure

```
litematica-enhancements/
├── gradle/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── yourname/
│       │           └── litematicaenhancements/
│       │               ├── LitematicaEnhancements.java (main mod class)
│       │               ├── materials/
│       │               │   ├── MaterialListCustom.java
│       │               │   └── MaterialListItemCache.java
│       │               ├── mixin/
│       │               │   ├── MixinGuiSchematicLoad.java
│       │               │   ├── MixinGuiMaterialList.java
│       │               │   ├── MixinHandledScreen.java
│       │               │   ├── MixinMaterialListBase.java
│       │               │   ├── MixinMaterialListSorter.java
│       │               │   ├── MixinWidgetMaterialListEntry.java
│       │               │   └── MixinWidgetSchematicBrowser.java
│       │               └── util/
│       │                   └── MaterialListUtils.java
│       └── resources/
│           ├── fabric.mod.json
│           ├── litematicaenhancements.mixins.json
│           └── assets/
│               └── litematicaenhancements/
│                   └── lang/
│                       └── en_us.json
├── build.gradle
└── gradle.properties
```

## Gradle Setup

### build.gradle

```gradle
plugins {
    id 'fabric-loom' version '1.13.4'
    id 'maven-publish'
}

version = project.mod_version
group = project.maven_group

repositories {
    mavenCentral()
    maven {
        name = "masa-maven"
        url = "https://masa.dy.fi/maven"
    }
    maven {
        name = "Terraformers"
        url = "https://maven.terraformersmc.com/"
    }
}

dependencies {
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"

    // Malilib and Litematica dependencies
    modImplementation "fi.dy.masa.malilib:malilib-fabric-1.21:${project.malilib_version}"
    modImplementation "fi.dy.masa.litematica:litematica-fabric-1.21.10:${project.litematica_version}"
}

processResources {
    inputs.property "version", project.version

    filesMatching("fabric.mod.json") {
        expand "version": project.version
    }
}

tasks.withType(JavaCompile).configureEach {
    it.options.release = 21
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

jar {
    from("LICENSE") {
        rename { "${it}_${project.archivesBaseName}"}
    }
}
```

### gradle.properties

```properties
# Minecraft/Fabric
minecraft_version=1.21.10
yarn_mappings=1.21.10+build.1
loader_version=0.16.11

# Fabric API
fabric_version=0.110.0+1.21.10

# Malilib and Litematica
malilib_version=0.24.0
litematica_version=0.24.5

# Mod Properties
mod_version=1.0.0
maven_group=com.yourname.litematicaenhancements
archives_base_name=litematica-enhancements

# Java
org.gradle.jvmargs=-Xmx2G
```

## fabric.mod.json

```json
{
  "schemaVersion": 1,
  "id": "litematica-enhancements",
  "version": "${version}",
  "name": "Litematica Enhancements",
  "description": "Adds custom material lists and smart cache-based sorting to Litematica",
  "authors": [
    "YourName"
  ],
  "contact": {
    "homepage": "https://github.com/yourname/litematica-enhancements",
    "sources": "https://github.com/yourname/litematica-enhancements"
  },
  "license": "MIT",
  "icon": "assets/litematicaenhancements/icon.png",
  "environment": "*",
  "entrypoints": {
    "client": [
      "com.yourname.litematicaenhancements.LitematicaEnhancements"
    ]
  },
  "mixins": [
    "litematicaenhancements.mixins.json"
  ],
  "depends": {
    "fabricloader": ">=0.16.0",
    "minecraft": "~1.21.10",
    "java": ">=21",
    "litematica": ">=0.24.0",
    "malilib": ">=0.24.0"
  },
  "suggests": {
    "fabric-api": "*"
  }
}
```

## litematicaenhancements.mixins.json

```json
{
  "required": true,
  "minVersion": "0.8",
  "package": "com.yourname.litematicaenhancements.mixin",
  "compatibilityLevel": "JAVA_21",
  "client": [
    "MixinGuiSchematicLoad",
    "MixinGuiMaterialList",
    "MixinHandledScreen",
    "MixinMaterialListBase",
    "MixinMaterialListSorter",
    "MixinWidgetMaterialListEntry",
    "MixinWidgetSchematicBrowser"
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
```

## Main Mod Class

### LitematicaEnhancements.java

```java
package com.yourname.litematicaenhancements;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LitematicaEnhancements implements ClientModInitializer
{
    public static final String MOD_ID = "litematica-enhancements";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient()
    {
        LOGGER.info("Litematica Enhancements initialized!");
    }
}
```

## Core Classes (Non-Mixin)

### MaterialListCustom.java

Copy the entire MaterialListCustom.java file you already created, but change the package to:
```java
package com.yourname.litematicaenhancements.materials;
```

### MaterialListItemCache.java

Copy the entire MaterialListItemCache.java file you already created, but change the package to:
```java
package com.yourname.litematicaenhancements.materials;
```

## Mixin Implementations

### MixinMaterialListBase.java

This mixin adds the CACHE_ORDER enum value and changes the default sort criteria.

```java
package com.yourname.litematicaenhancements.mixin;

import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListBase.SortCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MaterialListBase.class, remap = false)
public class MixinMaterialListBase
{
    @Shadow
    protected SortCriteria sortCriteria;

    // Change default sort criteria to CACHE_ORDER
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci)
    {
        // Use reflection or accessor to set CACHE_ORDER
        // Since we can't add enum values, we need a different approach
        // See "Handling the Enum Problem" section below
    }
}
```

**PROBLEM:** You **cannot add enum values** via mixins. See the "Handling the Enum Problem" section below for solutions.

### MixinMaterialListSorter.java

This mixin adds cache-based sorting logic.

```java
package com.yourname.litematicaenhancements.mixin;

import com.yourname.litematicaenhancements.materials.MaterialListItemCache;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListBase.SortCriteria;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import fi.dy.masa.litematica.materials.MaterialListSorter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MaterialListSorter.class, remap = false)
public class MixinMaterialListSorter
{
    @Shadow @Final
    private MaterialListBase materialList;

    @Inject(method = "compare", at = @At("HEAD"), cancellable = true)
    private void onCompare(MaterialListEntry entry1, MaterialListEntry entry2, CallbackInfoReturnable<Integer> cir)
    {
        SortCriteria sortCriteria = this.materialList.getSortCriteria();

        // Check if we're in "cache order" mode
        // Since we can't add CACHE_ORDER enum, use a workaround (see below)
        if (isCacheOrderMode(sortCriteria))
        {
            boolean reverse = this.materialList.getSortInReverse();
            String nameCompare = entry1.getStack().getName().getString()
                                       .compareTo(entry2.getStack().getName().getString());

            MaterialListItemCache cache = MaterialListItemCache.getInstance();
            int priority1 = cache.getCachePriority(entry1.getStack());
            int priority2 = cache.getCachePriority(entry2.getStack());

            if (priority1 == priority2)
            {
                cir.setReturnValue(nameCompare);
                return;
            }

            cir.setReturnValue((priority1 < priority2) != reverse ? -1 : 1);
        }
    }

    private boolean isCacheOrderMode(SortCriteria criteria)
    {
        // See "Handling the Enum Problem" section
        return false; // Placeholder
    }
}
```

### MixinHandledScreen.java

```java
package com.yourname.litematicaenhancements.mixin;

import com.yourname.litematicaenhancements.materials.MaterialListItemCache;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends Screen
{
    @Unique
    private boolean litematicaEnhancements_containerScanned = false;

    protected MixinHandledScreen(Text title)
    {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        if (!this.litematicaEnhancements_containerScanned)
        {
            HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
            MaterialListItemCache.getInstance().scanContainer(screen.getScreenHandler().slots);
            this.litematicaEnhancements_containerScanned = true;
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci)
    {
        this.litematicaEnhancements_containerScanned = false;
    }
}
```

### MixinWidgetSchematicBrowser.java

Adds support for .json and .txt files in the file browser.

```java
package com.yourname.litematicaenhancements.mixin;

import fi.dy.masa.litematica.gui.widgets.WidgetSchematicBrowser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileFilter;

@Mixin(value = WidgetSchematicBrowser.FileFilterSchematics.class, remap = false)
public class MixinWidgetSchematicBrowser
{
    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void onAccept(File file, CallbackInfoReturnable<Boolean> cir)
    {
        if (file.isFile())
        {
            String name = file.getName();
            if (name.endsWith(".json") || name.endsWith(".txt"))
            {
                cir.setReturnValue(true);
            }
        }
    }
}
```

### MixinGuiSchematicLoad.java

Handles loading custom material lists from JSON/TXT files.

```java
package com.yourname.litematicaenhancements.mixin;

import com.yourname.litematicaenhancements.materials.MaterialListCustom;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.gui.GuiSchematicLoad;
import fi.dy.masa.litematica.gui.GuiMaterialList;
import fi.dy.masa.litematica.schematic.util.FileType;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(value = GuiSchematicLoad.class, remap = false)
public class MixinGuiSchematicLoad
{
    @Inject(method = "loadMaterialList", at = @At("HEAD"), cancellable = true)
    private void onLoadMaterialList(Path file, FileType fileType, CallbackInfo ci)
    {
        if (fileType == FileType.JSON || fileType.name().equals("TXT"))
        {
            MaterialListCustom customList = MaterialListCustom.fromFile(file);

            if (customList != null)
            {
                DataManager.setMaterialList(customList);
                GuiBase.openGui(new GuiMaterialList(customList));
                // Add success message
                ci.cancel();
            }
        }
    }
}
```

### MixinGuiMaterialList.java

Adds export button for custom JSON format.

```java
package com.yourname.litematicaenhancements.mixin;

import com.yourname.litematicaenhancements.materials.MaterialListCustom;
import fi.dy.masa.litematica.gui.GuiMaterialList;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.ItemType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = GuiMaterialList.class, remap = false)
public class MixinGuiMaterialList
{
    @Shadow @Final
    private MaterialListBase materialList;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void addExportButton(CallbackInfo ci)
    {
        // Add export to JSON button
        // This requires knowing the GUI layout to position the button correctly
        // See the actual GuiMaterialList code for button positioning
    }
}
```

### MixinWidgetMaterialListEntry.java

Adds cache order cycling to column clicks.

```java
package com.yourname.litematicaenhancements.mixin;

import fi.dy.masa.litematica.gui.widgets.WidgetMaterialListEntry;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListBase.SortCriteria;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WidgetMaterialListEntry.class, remap = false)
public class MixinWidgetMaterialListEntry
{
    @Shadow @Final
    private MaterialListBase materialList;

    @Inject(method = "onMouseClickedImpl", at = @At(value = "INVOKE",
            target = "Lfi/dy/masa/litematica/materials/MaterialListBase;setSortCriteria(Lfi/dy/masa/litematica/materials/MaterialListBase$SortCriteria;)V",
            ordinal = 0), cancellable = true)
    private void onColumnClick(CallbackInfoReturnable<Boolean> cir)
    {
        // Modify column 0 click behavior to cycle through NAME and CACHE_ORDER
        // See "Handling the Enum Problem" section
    }
}
```

## Handling the Enum Problem

**Major Issue:** You **cannot add enum values** (like CACHE_ORDER) to Litematica's SortCriteria enum via mixins.

### Solution Options:

#### Option 1: Use Existing Enum Value as Trigger

**Use NAME as the cache order trigger with a special flag:**

```java
public class MaterialListItemCache
{
    private static boolean useCacheOrdering = true;

    public static void setUseCacheOrdering(boolean use)
    {
        useCacheOrdering = use;
    }

    public static boolean isUseCacheOrdering()
    {
        return useCacheOrdering;
    }
}
```

Then in MixinMaterialListSorter:
```java
@Inject(method = "compare", at = @At("HEAD"), cancellable = true)
private void onCompare(MaterialListEntry entry1, MaterialListEntry entry2, CallbackInfoReturnable<Integer> cir)
{
    if (MaterialListItemCache.isUseCacheOrdering())
    {
        // Do cache-based sorting
        // ... cache sorting logic
        cir.setReturnValue(result);
    }
    // Otherwise let normal sorting happen
}
```

Add a button to toggle cache ordering on/off.

#### Option 2: Shadow Expand (Advanced)

Use ASM to expand the enum at runtime:

```java
// This is VERY advanced and fragile
// Not recommended unless you're experienced with ASM
```

#### Option 3: Wrapper Pattern

Create a wrapper around MaterialListBase that intercepts sorting:

```java
public class EnhancedMaterialList
{
    private final MaterialListBase wrapped;
    private boolean useCacheOrder = true;

    public List<MaterialListEntry> getSortedEntries()
    {
        List<MaterialListEntry> entries = wrapped.getMaterialsFiltered(true);

        if (useCacheOrder)
        {
            entries.sort(new CacheComparator());
        }

        return entries;
    }
}
```

#### **Recommended: Option 1 (Toggle Flag)**

This is the simplest and most maintainable approach:

1. Add a toggle button in GuiMaterialList: "Cache Sorting: ON/OFF"
2. When ON, intercept all sorting in MixinMaterialListSorter to use cache
3. When OFF, let normal sorting happen
4. Store the toggle state in MaterialListItemCache singleton

## Complete Example: Toggle-Based Implementation

### Updated MaterialListItemCache.java

```java
package com.yourname.litematicaenhancements.materials;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import fi.dy.masa.malilib.util.ItemType;

public class MaterialListItemCache
{
    private static final MaterialListItemCache INSTANCE = new MaterialListItemCache();
    private final LinkedHashSet<ItemType> cachedItems = new LinkedHashSet<>();
    private boolean enabled = true;
    private boolean useCacheOrdering = true;  // NEW

    private MaterialListItemCache() {}

    public static MaterialListItemCache getInstance()
    {
        return INSTANCE;
    }

    public void setUseCacheOrdering(boolean use)  // NEW
    {
        this.useCacheOrdering = use;
    }

    public boolean isUseCacheOrdering()  // NEW
    {
        return this.useCacheOrdering;
    }

    public void scanContainer(List<Slot> slots)
    {
        if (!this.enabled)
        {
            return;
        }

        List<ItemType> foundItems = new ArrayList<>();

        for (Slot slot : slots)
        {
            if (slot.hasStack())
            {
                ItemStack stack = slot.getStack();
                ItemType itemType = new ItemType(stack, false, false);

                if (!foundItems.contains(itemType))
                {
                    foundItems.add(itemType);
                }
            }
        }

        for (ItemType itemType : foundItems)
        {
            this.cachedItems.remove(itemType);
            this.cachedItems.add(itemType);
        }
    }

    public int getCachePriority(ItemStack stack)
    {
        if (!this.enabled || !this.useCacheOrdering)
        {
            return Integer.MAX_VALUE;
        }

        ItemType itemType = new ItemType(stack, false, false);
        List<ItemType> items = new ArrayList<>(this.cachedItems);

        for (int i = items.size() - 1; i >= 0; i--)
        {
            if (items.get(i).equals(itemType))
            {
                return items.size() - 1 - i;
            }
        }

        return Integer.MAX_VALUE;
    }

    public void clear()
    {
        this.cachedItems.clear();
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;

        if (!enabled)
        {
            this.clear();
        }
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public int getCacheSize()
    {
        return this.cachedItems.size();
    }
}
```

### Updated MixinMaterialListSorter.java

```java
package com.yourname.litematicaenhancements.mixin;

import com.yourname.litematicaenhancements.materials.MaterialListItemCache;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import fi.dy.masa.litematica.materials.MaterialListSorter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MaterialListSorter.class, remap = false)
public class MixinMaterialListSorter
{
    @Shadow @Final
    private MaterialListBase materialList;

    @Inject(method = "compare", at = @At("HEAD"), cancellable = true)
    private void useCacheOrdering(MaterialListEntry entry1, MaterialListEntry entry2, CallbackInfoReturnable<Integer> cir)
    {
        MaterialListItemCache cache = MaterialListItemCache.getInstance();

        // If cache ordering is enabled, override all sorting
        if (cache.isUseCacheOrdering())
        {
            boolean reverse = this.materialList.getSortInReverse();
            int nameCompare = entry1.getStack().getName().getString()
                                   .compareTo(entry2.getStack().getName().getString());

            int priority1 = cache.getCachePriority(entry1.getStack());
            int priority2 = cache.getCachePriority(entry2.getStack());

            if (priority1 == priority2)
            {
                cir.setReturnValue(nameCompare);
                return;
            }

            cir.setReturnValue((priority1 < priority2) != reverse ? -1 : 1);
        }
    }
}
```

### Add Toggle Button in MixinGuiMaterialList.java

```java
package com.yourname.litematicaenhancements.mixin;

import com.yourname.litematicaenhancements.materials.MaterialListItemCache;
import fi.dy.masa.litematica.gui.GuiMaterialList;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMaterialList.class, remap = false)
public class MixinGuiMaterialList
{
    @Inject(method = "initGui", at = @At("TAIL"))
    private void addCacheToggleButton(CallbackInfo ci)
    {
        MaterialListItemCache cache = MaterialListItemCache.getInstance();

        String label = cache.isUseCacheOrdering() ? "Cache Sort: ON" : "Cache Sort: OFF";

        ButtonGeneric button = new ButtonGeneric(10, 10, -1, 20, label);
        button.setActionListener((btn, mouseButton) -> {
            cache.setUseCacheOrdering(!cache.isUseCacheOrdering());
            btn.updateDisplayString();
            // Refresh the list widget
        });

        // Add button to screen
        // Note: You'll need to access the button list via @Shadow or accessor
    }
}
```

## Language File

### en_us.json

```json
{
  "litematica.enhancements.button.cache_sort_on": "Cache Sort: ON",
  "litematica.enhancements.button.cache_sort_off": "Cache Sort: OFF",
  "litematica.enhancements.button.export_json": "Export to JSON",
  "litematica.enhancements.message.exported": "Exported material list to %s",
  "litematica.enhancements.message.loaded": "Loaded custom material list from %s",
  "litematica.enhancements.message.export_failed": "Failed to export material list",
  "litematica.enhancements.message.load_failed": "Failed to load custom material list"
}
```

## Building and Testing

### Build Commands

```bash
./gradlew build
```

The output JAR will be in `build/libs/litematica-enhancements-1.0.0.jar`

### Installation

1. Place `litematica-enhancements-1.0.0.jar` in your `.minecraft/mods/` folder
2. Ensure Litematica and Malilib are also installed
3. Launch Minecraft

### Testing Checklist

- [ ] Mod loads without crashes
- [ ] Cache sorting can be toggled on/off
- [ ] Opening containers scans items
- [ ] Material list shows cached items first when enabled
- [ ] .json and .txt files appear in schematic browser
- [ ] Custom material lists can be loaded
- [ ] Material lists can be exported to JSON

## Advantages of Addon Approach

1. **Independent Development** - No need to wait for Litematica maintainer
2. **Easy Updates** - Update your addon when Litematica updates
3. **User Choice** - Users can install the addon or not
4. **Cleaner Separation** - Your code stays separate from Litematica
5. **Easier Maintenance** - You control your codebase entirely

## Potential Issues and Solutions

### Issue: Mixin Conflicts
**Solution:** Use unique mixin method names with your mod ID prefix

### Issue: Litematica API Changes
**Solution:** Version your addon to specific Litematica versions

### Issue: Enum Extension
**Solution:** Use the toggle flag approach instead of trying to add enum values

### Issue: Access to Private Fields
**Solution:** Use `@Shadow` or create `@Accessor` mixins

## Advanced: Accessor Mixins

For accessing private Litematica fields:

```java
@Mixin(value = GuiMaterialList.class, remap = false)
public interface AccessorGuiMaterialList
{
    @Accessor("materialList")
    MaterialListBase getMaterialList();

    @Accessor("listWidget")
    WidgetListMaterialList getListWidget();
}
```

Usage:
```java
GuiMaterialList gui = ...;
MaterialListBase list = ((AccessorGuiMaterialList) gui).getMaterialList();
```

## Summary

The addon approach allows you to:
1. Keep your code separate from Litematica
2. Distribute your enhancements independently
3. Avoid dealing with the main Litematica codebase maintainer
4. Update on your own schedule

The **toggle flag approach** sidesteps the enum limitation while providing all the functionality you need.
