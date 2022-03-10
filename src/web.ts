import { WebPlugin } from '@capacitor/core';

import type { HeadsetPluginSetActiveOptions, HeadsetPluginStartOptions, IHeadsetPlugin } from './definitions';

export class HeadsetPluginWeb extends WebPlugin implements IHeadsetPlugin {
  async checkPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('Not implemented on web.');
  }

  async requestPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('Not implemented on web.');
  }
  
  async start(options: HeadsetPluginStartOptions): Promise<void> {
    throw this.unimplemented('Not implemented on web. ' + options.type);
  }

  async stop(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async setActive(options: HeadsetPluginSetActiveOptions): Promise<void> {
    throw this.unimplemented('Not implemented on web.' + options.isActive);
  }
}
