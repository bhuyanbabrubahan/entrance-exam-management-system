import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "path"; // <-- add this

export default defineConfig({
  plugins: [
    react({
      babel: {
        plugins: [["babel-plugin-react-compiler"]],
      },
    }),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"), // <-- THIS LINE maps @ to src folder
    },
  },
});