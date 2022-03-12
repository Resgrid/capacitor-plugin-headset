import { WebPlugin } from '@capacitor/core';

import type { HeadsetPluginSetActiveOptions, HeadsetPluginStartOptions, IHeadsetPlugin, SetAudioModeOptions, ToggleBluetoothScoOptions, ToggleSpeakerphoneOptions } from './definitions';

export class HeadsetPluginWeb extends WebPlugin implements IHeadsetPlugin {
  setAudioMode(options: SetAudioModeOptions): Promise<void> {
    throw new Error('Not implemented on web: ' + options.audioMode);
  }
  toggleBluetoothSco(options: ToggleBluetoothScoOptions): Promise<void> {
    throw new Error('Not implemented on web: ' + options.scoOn);
  }
  toggleSpeakerphone(options: ToggleSpeakerphoneOptions): Promise<void> {
    throw new Error('Not implemented on web: ' + options.speakerphoneOn);
  }
  getOutputDevices(): Promise<any> {
    throw new Error('Not implemented on web.');
  }
  getAudioMode(): Promise<{ mode: string; }> {
    throw new Error('Not implemented on web.');
  }
  isSpeakerphoneOn(): Promise<{ speakerphoneOn: boolean; }> {
    throw new Error('Not implemented on web.');
  }
  isBluetoothScoOn(): Promise<{ bluetoothScoOn: boolean; }> {
    throw new Error('Not implemented on web.');
  }
  hasBuiltInEarpiece(): Promise<{ builtInEarpiece: boolean; }> {
    throw new Error('Not implemented on web.');
  }
  hasBuiltInSpeaker(): Promise<{ builtInSpeaker: boolean; }> {
    throw new Error('Not implemented on web.');
  }
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
