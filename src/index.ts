import { registerPlugin } from '@capacitor/core';

import type { IHeadsetPlugin } from './definitions';

const HeadsetPlugin = registerPlugin<IHeadsetPlugin>('HeadsetPlugin', {
  web: () => import('./web').then(m => new m.HeadsetPluginWeb()),
});

export * from './definitions';
export { HeadsetPlugin };
