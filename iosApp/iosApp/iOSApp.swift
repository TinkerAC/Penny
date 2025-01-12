import SwiftUI

import FirebaseCore
import FirebaseMessaging

@main
struct iOSApp: App {


    init(){
        FirebaseApp.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
