import { WebPlugin } from '@capacitor/core';

import type { HeadsetPluginStartOptions, IHeadsetPlugin } from './definitions';

export class HeadsetPluginWeb extends WebPlugin implements IHeadsetPlugin {
  async start(options: HeadsetPluginStartOptions): Promise<void> {
    console.log('start '+ options.type);
  }

  async stop(): Promise<void> {
    console.log('stop');
  }
}
