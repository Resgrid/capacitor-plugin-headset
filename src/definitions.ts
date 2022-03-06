import type { PluginListenerHandle } from "@capacitor/core";

export interface IHeadsetPlugin {
  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;
  start(options: HeadsetPluginStartOptions): Promise<void>;
  stop(): Promise<void>;
  setActive(options: HeadsetPluginSetActiveOptions): Promise<void>;
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