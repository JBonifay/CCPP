import { definePreset } from '@primeuix/themes';
import Lara from '@primeuix/themes/lara';

export const customerPreset = definePreset(Lara, {
  semantic: {
    primary: {
      50: '#FFF9E5',  // very light yellow
      100: '#FFF3CC',
      200: '#FFE699',
      300: '#FFD966',
      400: '#FFCC33',
      500: '#FFBF00',  // main yellow
      600: '#E6A800',
      700: '#CC9100',
      800: '#B37900',
      900: '#995F00',
      950: '#664000'
    },
    secondary: {
      50: '#F3E5FF', // light purple
      100: '#E6CCFF',
      200: '#CC99FF',
      300: '#B266FF',
      400: '#9933FF',
      500: '#8000FF', // main purple
      600: '#7300E6',
      700: '#6600CC',
      800: '#5900B3',
      900: '#400080',
      950: '#26004D'
    },
    accent: {
      500: '#FF3333' // red accent (heart)
    },
    neutral: {
      50: '#F5F5F5',
      100: '#E0E0E0',
      200: '#C2C2C2',
      300: '#A3A3A3',
      400: '#858585',
      500: '#666666',
      600: '#4D4D4D',
      700: '#333333',
      800: '#1A1A1A',
      900: '#0D0D0D'
    }
  }
});
