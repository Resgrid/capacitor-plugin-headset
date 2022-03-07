import Foundation
import Capacitor
import AVFoundation

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(HeadsetPlugin)
public class HeadsetPlugin: CAPPlugin {
    
    @objc override public func checkPermissions(_ call: CAPPluginCall) {
            var result: [String: Any] = [:]
            for permission in MicrophonePermissionType.allCases {
                let state: String
                switch permission {
                case .microphone:
                    state = AVCaptureDevice.authorizationStatus(for: .audio).authorizationState
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
    
}
