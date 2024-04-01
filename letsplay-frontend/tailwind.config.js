/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
    "./node_modules/flowbite/**/*.js" 
  ],
  theme: {
    extend: {
      colors: {
        backgroundMain: '#C1BABA',
        backgroundTile: '#D9D9D9'
      },
      textShadow: {
        sm: '0 1px 2px var(--tw-shadow-color)',
        DEFAULT: '0 2px 4px var(--tw-shadow-color)',
        lg: '0 8px 16px var(--tw-shadow-color)',
      },
      card: {
        backgroundColor: '#D9D9D9',
        borderRadius: '0.5rem',
        boxShadow: '0px 4px 4px 0px rgba(0, 0, 0, 0.25)',
        padding: '2rem',
        // Ajoutez d'autres propriétés CSS au besoin
      },
    },
  },
  plugins: [
    require('flowbite/plugin')
  ],
};
