#!/bin/sh

pandoc -H disable_float.tex -V geometry:margin=1in ./coral-duchatel-dutilleul-mission7.md -o mission7-compte-rendu.pdf

xdg-open ./mission7-compte-rendu.pdf