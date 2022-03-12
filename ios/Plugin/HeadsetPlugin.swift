import Foundation
import Capacitor
import AVFoundation

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(HeadsetPlugin)
public class HeadsetPlugin: CAPPlugin {
    //var audioController: AudioController?;
    
    public override func load() {
        //self.audioController = AudioController()
        AudioController.init()
    }
    
    @objc override public func checkPermissions(_ call: CAPPluginCall) {
            var result: [String: Any] = [:]
            for permission in MicrophonePermissionType.allCases {
                let state: String
                switch permission {
                case .microphone:
                    state = String(AVCaptureDevice.authorizationStatus(for: .audio).rawValue)
                }
                result[permission.rawValue] = state
            }
            call.resolve(result)
        }
    
    
    @objc override public func requestPermissions(_ call: CAPPluginCall) {
            let permissions: [MicrophonePermissionType] = MicrophonePermissionType.allCases
            
            let group = DispatchGroup()
            for permission in permissions {
                switch permission {
                case .microphone:
                    group.enter()
                    AVCaptureDevice.requestAccess(for: .audio) { _ in
                        group.leave()
                    }
                }
            }
            group.notify(queue: DispatchQueue.main) { [weak self] in
                self?.checkPermissions(call)
            }
        }
    
    @objc func start(_ call: CAPPluginCall) {
        call.resolve();
    }
    
    @objc func stop(_ call: CAPPluginCall) {
        call.resolve();
    }
    
    @objc func setActive(_ call: CAPPluginCall) {
        call.resolve();
    }
    
    @objc func setAudioMode(_ call: CAPPluginCall) {
        let audioMode = call.getString("audioMode") ?? "speaker"
        
        if (audioMode == "earpiece") {
            AudioController.selectAudioOutputEarpiece()
        } else if (audioMode == "speaker" || audioMode == "ringtone") {
            AudioController.selectAudioOutputSpeaker()
        } else if (audioMode == "normal") {
            AudioController.selectAudioOutputEarpiece()
        }
        
        call.resolve();
    }
    
    @objc func toggleBluetoothSco(_ call: CAPPluginCall) {
        let audioMode = call.getBool("scoOn") ?? false
        call.resolve();
    }
    
    @objc func toggleSpeakerphone(_ call: CAPPluginCall) {
        let audioMode = call.getBool("speakerphoneOn") ?? false
        call.resolve();
    }
    
    @objc func getOutputDevices(_ call: CAPPluginCall) {
        call.resolve();
    }
    
    @objc func getAudioMode(_ call: CAPPluginCall) {
        call.resolve();
    }
    
    @objc func isSpeakerphoneOn(_ call: CAPPluginCall) {
        call.resolve();
    }
    
    @objc func isBluetoothScoOn(_ call: CAPPluginCall) {
        call.resolve();
    }
    
    @objc func hasBuiltInEarpiece(_ call: CAPPluginCall) {
        call.resolve();
    }
    
    @objc func hasBuiltInSpeaker(_ call: CAPPluginCall) {
        call.resolve();
    }
}
