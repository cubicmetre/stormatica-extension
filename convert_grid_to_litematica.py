#!/usr/bin/env python3
"""
Convert grid-based JSON format to Litematica-compatible format with cell groupings.

Input format (cubic1.json, cubic2.json):
[
  ["stone", "dirt", null, ...],
  ["grass_block", "cobblestone", null, ...]
]

Output format (litematica-compatible):
{
  "name": "Converted Layout",
  "items": [
    {"id": "minecraft:stone", "count": 1, "cell": 0},
    {"id": "minecraft:dirt", "count": 1, "cell": 0},
    {"id": "minecraft:grass_block", "count": 1, "cell": 1},
    {"id": "minecraft:cobblestone", "count": 1, "cell": 1}
  ]
}
"""

import json
import sys
from pathlib import Path


def convert_grid_to_litematica(input_file: Path, output_file: Path, name: str = None, default_count: int = 1):
    """Convert grid JSON to Litematica-compatible format."""

    # Read input file
    with open(input_file, 'r') as f:
        grid = json.load(f)

    if not isinstance(grid, list):
        print(f"Error: Expected array at root, got {type(grid).__name__}", file=sys.stderr)
        return False

    # Build items list with cell groupings
    items = []

    for cell_index, cell_items in enumerate(grid):
        if not isinstance(cell_items, list):
            print(f"Warning: Skipping non-array cell at index {cell_index}", file=sys.stderr)
            continue

        for item_id in cell_items:
            if item_id is None:
                continue  # Skip null/empty slots

            # Add minecraft: namespace if not present
            if ":" not in item_id:
                item_id = f"minecraft:{item_id}"

            items.append({
                "id": item_id,
                "count": default_count,
                "cell": cell_index
            })

    # Create output structure
    if name is None:
        name = input_file.stem

    output = {
        "name": name,
        "items": items
    }

    # Write output file
    with open(output_file, 'w') as f:
        json.dump(output, f, indent=2)

    print(f"Converted {len(items)} items from {len(grid)} cells")
    print(f"  Input:  {input_file}")
    print(f"  Output: {output_file}")

    return True


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python convert_grid_to_litematica.py <input.json> [output.json] [name] [default_count]")
        print()
        print("Examples:")
        print("  python convert_grid_to_litematica.py cubic1.json")
        print("  python convert_grid_to_litematica.py cubic1.json cubic1_converted.json")
        print("  python convert_grid_to_litematica.py cubic1.json cubic1_converted.json \"Cubic Sorter 1\"")
        print("  python convert_grid_to_litematica.py cubic1.json cubic1_converted.json \"Cubic Sorter 1\" 64")
        sys.exit(1)

    input_file = Path(sys.argv[1])

    if not input_file.exists():
        print(f"Error: Input file '{input_file}' not found", file=sys.stderr)
        sys.exit(1)

    # Output file defaults to input_converted.json
    if len(sys.argv) >= 3:
        output_file = Path(sys.argv[2])
    else:
        output_file = input_file.with_stem(f"{input_file.stem}_converted")

    # Name defaults to input filename
    name = sys.argv[3] if len(sys.argv) >= 4 else None

    # Default count defaults to 1
    default_count = int(sys.argv[4]) if len(sys.argv) >= 5 else 1

    success = convert_grid_to_litematica(input_file, output_file, name, default_count)
    sys.exit(0 if success else 1)
