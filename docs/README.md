# Kaumei JDBC documentation

This directory contains the Astro + Starlight site for Kaumei JDBC.
Use it to document configuration, integration advice, and processor behaviour.

## Documentation conventions

- Write in British English to keep terminology consistent.
- Wrap Markdown lines to roughly 80–100 characters to minimise merge noise.
- Start each new sentence on a new line to make diffs easier to review.
- Keep tone instructional, concise, and focused on Kaumei JDBC scenarios.
- Prefer code fences for commands and configuration fragments.

## Project layout

Source lives below `src/content/docs`, while shared assets belong in `src/assets`.
Static files ship from `public`.
The Astro configuration stays in `astro.config.mjs`, and Starlight specific settings
live in `markdoc.config.mjs` plus `ec.config.mjs`.

## Commands

Run all commands from this `docs` directory.
Develop locally with:

```
npm install
npm run dev
```

export ASTRO_PREVIEW=1
export ASTRO_SITE=https://kaumei.github.io/kaumei-jdbc/
export ASTRO_BASE=/kaumei-jdbc/


Produce a build with `npm run build`.
Preview the generated site via `npm run preview`.
