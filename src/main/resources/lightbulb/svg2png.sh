#!/bin/bash
set -euo pipefail

if [ $# -ne 1 ]; then
  echo "Usage: $0 <input.svg>"
  exit 1
fi

input="$1"
if [[ "${input##*.}" != "svg" ]]; then
  echo "Error: input file must have .svg extension"
  exit 1
fi

# derive output filename by replacing .svg → .png
dir="$(dirname "$input")"
base="$(basename "$input" .svg)"
output="$dir/$base.png"

# invoke inkscape
inkscape "$input" \
  --export-type=png \
  --export-filename="$output" \
  --export-width=100 \
  --export-height=100
