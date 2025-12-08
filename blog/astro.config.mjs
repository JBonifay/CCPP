// @ts-check
import { defineConfig } from 'astro/config';
import mdx from '@astrojs/mdx';

// https://astro.build/config
export default defineConfig({
  integrations: [mdx()],
  // GitHub Pages deployment configuration
  site: 'https://jbonifay.github.io',
  base: '/CCPP',
});
