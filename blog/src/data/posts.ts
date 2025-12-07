export interface Post {
  number: number;
  title: string;
  date: string;
  excerpt: string;
  topics: string[];
  color: string;
  slug: string;
}

export const posts: Post[] = [
  {
    number: 2,
    title: "What I've learned",
    date: "February 7, 2024",
    excerpt: "At my job, every engineer does customer support. You might think...",
    topics: ["MISC"],
    color: "#3b82f6", // blue
    slug: "what-ive-learned-2"
  },
  {
    number: 1,
    title: "What I've learned",
    date: "February 1, 2024",
    excerpt: "Our work at PostHog is public, so it's pretty cool to be able to share what we...",
    topics: ["MISC"],
    color: "#3b82f6", // blue
    slug: "what-ive-learned-1"
  },
  {
    number: 34,
    title: "Monitoring & logging",
    date: "April 18, 2023",
    excerpt: "Now that our simulation is running, let's set up monitoring and logging...",
    topics: ["NODE.JS", "DOCKER"],
    color: "#eab308", // yellow
    slug: "monitoring-logging"
  },
  {
    number: 33,
    title: "Finalizing the simulation",
    date: "March 27, 2023",
    excerpt: "Finally, the time has come to pull all the components of our simulation...",
    topics: ["NODE.JS"],
    color: "#eab308", // yellow
    slug: "finalizing-simulation"
  },
  {
    number: 32,
    title: "Route planner",
    date: "March 15, 2023",
    excerpt: "Now that a driver is matched with a customer, we need them to head t...",
    topics: ["GO"],
    color: "#3b82f6", // blue
    slug: "route-planner"
  },
  {
    number: 31,
    title: "Matching drivers with customers",
    date: "March 10, 2023",
    excerpt: "The next step in our simulation is matching drivers with customers. Every entry run...",
    topics: ["GO"],
    color: "#10b981", // green
    slug: "matching-drivers"
  },
  {
    number: 30,
    title: "Generating destinations",
    date: "March 8, 2023",
    excerpt: "Previously, we added functionality for generating customer locations...",
    topics: ["GO"],
    color: "#eab308", // yellow
    slug: "generating-destinations"
  },
  {
    number: 29,
    title: "Multiprocessing in Node.js",
    date: "February 24, 2023",
    excerpt: "Here's one crucial thing to consider for our simulation. Every entry run...",
    topics: ["NODE.JS"],
    color: "#eab308", // yellow
    slug: "multiprocessing-nodejs"
  },
  {
    number: 28,
    title: "Simulation engine",
    date: "February 22, 2023",
    excerpt: "Today, we are starting to build the simulation engine. Let's see how it...",
    topics: ["GO"],
    color: "#3b82f6", // blue
    slug: "simulation-engine"
  },
  {
    number: 27,
    title: "Animation fixes",
    date: "February 17, 2023",
    excerpt: "A few issues with our animation code only became apparent after I start...",
    topics: ["NODE.JS", "UI"],
    color: "#10b981", // green
    slug: "animation-fixes"
  },
  {
    number: 26,
    title: "Server-generated data",
    date: "February 16, 2023",
    excerpt: "Let's generate location updates on the backend and make them...",
    topics: ["GO", "NODE.JS"],
    color: "#eab308", // yellow
    slug: "server-generated-data"
  },
  {
    number: 25,
    title: "Tidying up",
    date: "February 14, 2023",
    excerpt: "We have iterated a fair bit on our project and a general structure is...",
    topics: ["NODE.JS"],
    color: "#eab308", // yellow
    slug: "tidying-up"
  },
  {
    number: 24,
    title: "Turning a car",
    date: "February 8, 2023",
    excerpt: "The only thing our car animation lacks is turns. Let's finish it up. A...",
    topics: ["NODE.JS", "UI"],
    color: "#3b82f6", // blue
    slug: "turning-car"
  },
  {
    number: 23,
    title: "Moving a car",
    date: "February 6, 2023",
    excerpt: "A car will move along a calculated path between the starting point an...",
    topics: ["NODE.JS", "UI"],
    color: "#eab308", // yellow
    slug: "moving-car"
  },
  {
    number: 22,
    title: "Designing a car",
    date: "February 3, 2023",
    excerpt: "Let's design a car icon we will place on the map. I used the amazing an...",
    topics: ["UI"],
    color: "#eab308", // yellow
    slug: "designing-car"
  },
  {
    number: 21,
    title: "Migrating to React",
    date: "January 30, 2023",
    excerpt: "I started out writing the frontend application in vanilla Javascript. Thi...",
    topics: ["NODE.JS"],
    color: "#eab308", // yellow
    slug: "migrating-react"
  },
  {
    number: 20,
    title: "Docker Compose in production",
    date: "January 27, 2023",
    excerpt: "We have been developing our application locally using Docker Compose...",
    topics: ["DOCKER"],
    color: "#3b82f6", // blue
    slug: "docker-compose-production"
  },
  {
    number: 19,
    title: "Connecting two containers",
    date: "January 25, 2023",
    excerpt: "Let's connect our frontend and backend containers so they can communicate...",
    topics: ["DOCKER"],
    color: "#10b981", // green
    slug: "connecting-containers"
  },
  {
    number: 18,
    title: "Go: database connection",
    date: "January 23, 2023",
    excerpt: "Today we'll set up a database connection in our Go backend...",
    topics: ["GO"],
    color: "#3b82f6", // blue
    slug: "go-database"
  },
  {
    number: 17,
    title: "SQL setup",
    date: "January 20, 2023",
    excerpt: "Let's set up our database schema and initial tables...",
    topics: ["SYSADMIN"],
    color: "#eab308", // yellow
    slug: "sql-setup"
  }
];
