import { defineCollection, z } from 'astro:content';

const posts = defineCollection({
  type: 'content',
  schema: z.object({
    number: z.number(),
    title: z.string(),
    date: z.string(),
    excerpt: z.string(),
    topics: z.array(z.string()),
    color: z.string(),
  }),
});

export const collections = { posts };
