<template>
  <router-view />
</template>

<style>
/* CSS Reset and Variables */
:root {
  --primary-color: #4f46e5;
  --primary-hover: #4338ca;
  --bg-gradient-start: #0f172a;
  --bg-gradient-end: #1e1b4b;
  --card-bg: rgba(255, 255, 255, 0.95);
  --text-main: #1f2937;
  --text-muted: #6b7280;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'Outfit', 'Inter', -apple-system, sans-serif;
  background-color: #f3f4f6;
  color: var(--text-main);
}
</style>
