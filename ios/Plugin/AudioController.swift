//
//  AudioController.swift
//  Plugin
//
//  Created by Shawn Jackson on 3/12/22.
//  Copyright © 2022 Max Lynch. All rights reserved.
//

import Foundation
import AVFoundation

class AudioController {

    static private var audioCategory : AVAudioSession.Category = AVAudioSession.Category.playAndRecord

    static private var audioCategoryOptions : AVAudioSession.CategoryOptions = [
        AVAudioSession.CategoryOptions.mixWithOthers,
        AVAudioSession.CategoryOptions.allowBluetooth,
        AVAudioSession.CategoryOptions.allowAirPlay,
        AVAudioSession.CategoryOptions.allowBluetoothA2DP
    ]

    /*
     This mode is intended for Voice over IP (VoIP) apps and can only be used with the playAndRecord category. When this mode is used, the device’s tonal equalization is optimized for voice and the set of allowable audio routes is reduced to only those appropriate for voice chat.
      See: https://developer.apple.com/documentation/avfoundation/avaudiosession/mode/1616455-voicechat
     */
    static private var audioMode = AVAudioSession.Mode.voiceChat
    static private var audioModeDefault : AVAudioSession.Mode = AVAudioSession.Mode.default

    static private var audioInputSelected: AVAudioSessionPortDescription? = nil

    //
    // Audio Input
    //
    static func initAudioDevices() -> Void {

        AudioController.setCategory()

        do {
            let audioSession: AVAudioSession = AVAudioSession.sharedInstance()
            try audioSession.setActive(true)
        } catch  {
            print("Error messing with audio session: \(error)")
        }
    }

    static func setCategory() -> Void {
        // Enable speaker
        NSLog("AudioController#setCategory()")

        do {
            let audioSession: AVAudioSession = AVAudioSession.sharedInstance()
            try audioSession.setCategory(
            AudioController.audioCategory,
                mode: AudioController.audioMode,
                options: AudioController.audioCategoryOptions
            )
        } catch {
            NSLog("AudioController#setCategory() | ERROR \(error)")
        };
    }

    // Setter function inserted by save specific audio device
    static func saveInputAudioDevice(inputDeviceUID: String) -> Void {
        let audioSession: AVAudioSession = AVAudioSession.sharedInstance()
        let audioInput: AVAudioSessionPortDescription = audioSession.availableInputs!.filter({
            (value:AVAudioSessionPortDescription) -> Bool in
            return value.uid == inputDeviceUID
        })[0]

        AudioController.audioInputSelected = audioInput
    }

    // Setter function inserted by set specific audio device
    static func restoreInputOutputAudioDevice() -> Void {
        let audioSession: AVAudioSession = AVAudioSession.sharedInstance()

        do {
            try audioSession.setPreferredInput(AudioController.audioInputSelected)
        } catch {
            NSLog("AudioController:restoreInputOutputAudioDevice: Error setting audioSession preferred input.")
        }

        AudioController.setOutputSpeakerIfNeed(enabled: speakerEnabled);
    }

    static func setOutputSpeakerIfNeed(enabled: Bool) {

        speakerEnabled = enabled

        let audioSession: AVAudioSession = AVAudioSession.sharedInstance()
        let currentRoute = audioSession.currentRoute

        if currentRoute.outputs.count != 0 {
            for description in currentRoute.outputs {
                if (
                    description.portType == AVAudioSession.Port.headphones ||
                        description.portType == AVAudioSession.Port.bluetoothA2DP ||
                            description.portType == AVAudioSession.Port.carAudio ||
                                description.portType == AVAudioSession.Port.airPlay ||
                                    description.portType == AVAudioSession.Port.lineOut
                ) {
                    NSLog("udioController#setOutputSpeakerIfNeed() | external audio output plugged in -> do nothing")
                } else {
                    NSLog("AudioController#setOutputSpeakerIfNeed() | external audio pulled out")

                    if (speakerEnabled) {
                        do {
                            try audioSession.overrideOutputAudioPort(AVAudioSession.PortOverride.speaker)
                        } catch {
                            NSLog("AudioController#setOutputSpeakerIfNeed() | ERROR \(error)")
                        };
                    }
                }
            }
        } else {
            NSLog("AudioController#setOutputSpeakerIfNeed() | requires connection to device")
        }
    }

    static func selectAudioOutputSpeaker() {
        // Enable speaker
        NSLog("AudioController#selectAudioOutputSpeaker()")

        speakerEnabled = true;

        setCategory()

        do {
            let audioSession: AVAudioSession = AVAudioSession.sharedInstance()
            try audioSession.overrideOutputAudioPort(AVAudioSession.PortOverride.speaker)
        } catch {
            NSLog("AudioController#selectAudioOutputSpeaker() | ERROR \(error)")
        };
    }

    static func selectAudioOutputEarpiece() {
        // Disable speaker, switched to default
        NSLog("AudioController#selectAudioOutputEarpiece()")

        speakerEnabled = false;

        setCategory()

        do {
            let audioSession: AVAudioSession = AVAudioSession.sharedInstance()
            try audioSession.overrideOutputAudioPort(AVAudioSession.PortOverride.none)
        } catch {
            NSLog("AudioController#selectAudioOutputEarpiece() | ERROR \(error)")
        };
    }

    //
    // Audio Output
    //
    static private var speakerEnabled: Bool = false

    init() {
        let shouldManualInit = Bundle.main.object(forInfoDictionaryKey: "ManualInitAudioDevice") as? String

        if(shouldManualInit == "FALSE") {
            AudioController.initAudioDevices()
        }

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(self.audioRouteChangeListener(_:)),
            name: AVAudioSession.routeChangeNotification,
            object: nil)
    }

    @objc dynamic fileprivate func audioRouteChangeListener(_ notification:Notification) {
        let audioRouteChangeReason = notification.userInfo![AVAudioSessionRouteChangeReasonKey] as! UInt

        switch audioRouteChangeReason {
        case AVAudioSession.RouteChangeReason.newDeviceAvailable.rawValue:
            NSLog("AudioController#audioRouteChangeListener() | headphone plugged in")
        case AVAudioSession.RouteChangeReason.oldDeviceUnavailable.rawValue:
            NSLog("AudioController#audioRouteChangeListener() | headphone pulled out -> restore state speakerEnabled: %@", AudioController.speakerEnabled ? "true" : "false")
            AudioController.setOutputSpeakerIfNeed(enabled: AudioController.speakerEnabled)
        default:
            break
        }
    }
}
