import type { PluginListenerHandle } from "@capacitor/core";

export interface IHeadsetPlugin {
  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;
  start(options: HeadsetPluginStartOptions): Promise<void>;
  stop(): Promise<void>;
  setActive(options: HeadsetPluginSetActiveOptions): Promise<void>;
  setAudioMode(options: SetAudioModeOptions): Promise<void>;
  toggleBluetoothSco(options: ToggleBluetoothScoOptions): Promise<void>;
  toggleSpeakerphone(options: ToggleSpeakerphoneOptions): Promise<void>;
  getOutputDevices(): Promise<any>;
  getAudioMode(): Promise<{ mode: string }>;
  isSpeakerphoneOn(): Promise<{ speakerphoneOn: boolean }>;
  isBluetoothScoOn(): Promise<{ bluetoothScoOn: boolean }>;
  hasBuiltInEarpiece(): Promise<{ builtInEarpiece: boolean }>;
  hasBuiltInSpeaker(): Promise<{ builtInSpeaker: boolean }>;
  addListener(eventName: 'onHeadsetPress', listenerFunc: (res: { event: string }) => void): PluginListenerHandle;
  addListener(eventName: 'onHeadsetRelease', listenerFunc: (res: { event: string }) => void): PluginListenerHandle;
  addListener(eventName: 'onHeadsetToggle', listenerFunc: (res: { event: string }) => void): PluginListenerHandle;
  addListener(eventName: 'onHeadsetConnected', listenerFunc: (res: { event: string }) => void): PluginListenerHandle;
  addListener(eventName: 'onHeadsetDisconnected', listenerFunc: (res: { event: string }) => void): PluginListenerHandle;
  addListener(eventName: 'onHeadsetVolUp', listenerFunc: (res: { event: string }) => void): PluginListenerHandle;
  addListener(eventName: 'onHeadsetVolDown', listenerFunc: (res: { event: string }) => void): PluginListenerHandle;
  addListener(eventName: 'onHeadsetEmergency', listenerFunc: (res: { event: string }) => void): PluginListenerHandle;
}

export interface HeadsetPluginStartOptions {
  /**
   * Type of headset to use.
   * 0 = RegularHeadsetToggle, 1 = LegacyPttHeadset, 2 = PttHeadset
   * 
   */
  type: number;
}

export interface HeadsetPluginSetActiveOptions {
  /**
   * Type of headset to use.
   * true there is an active communications channel (i.e. app is connected to audio/video)
   * false if no communications channel is active (i.e. app is not connected to audio/video)
   */
   isActive: boolean;
}

export interface SetAudioModeOptions {
  /**
   * Type of audio mode to set.
   * OPTIONS: bluetooth, earpiece, speaker, ringtone, normal
   */
   audioMode: string;
}

export interface ToggleBluetoothScoOptions {
  /**
   * Turn BluetoothSco On or Off
   */
   scoOn: boolean;
}

export interface ToggleSpeakerphoneOptions {
  /**
   * Turn speakerphone On or Off
   */
   speakerphoneOn: boolean;
}