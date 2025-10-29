# Trackier SDK Integration - TicTacToe Android App

This document provides a comprehensive overview of the Trackier SDK integration in the TicTacToe Android application, including all implemented features, event tracking, deep linking, and dynamic link functionality.

## 📋 Table of Contents

1. [SDK Initialization](#sdk-initialization)
2. [Event Tracking](#event-tracking)
3. [Deep Link Handling](#deep-link-handling)
4. [Dynamic Link Creation](#dynamic-link-creation)
5. [Deferred Deep Link Resolution](#deferred-deep-link-resolution)
6. [File Locations](#file-locations)
7. [Configuration](#configuration)
8. [Logging and Debugging](#logging-and-debugging)

## 🚀 SDK Initialization

### Location: `app/src/main/java/com/cloudstuff/tictactoe/TicTacToe.java`

The Trackier SDK is initialized in the Application class with the following configuration:

```java
// SDK Configuration
TrackierSDKConfig sdkConfig = new TrackierSDKConfig(this, TR_DEV_KEY, "development");
sdkConfig.setDeepLinkListener(deepLinkListener);
TrackierSDK.initialize(sdkConfig);
```

**Key Features:**
- Development environment configuration
- Deep link listener setup
- SDK initialization with custom configuration

## 📊 Event Tracking

### 1. App Launch Event
**Location:** `app/src/main/java/com/cloudstuff/tictactoe/activity/SplashActivity.java`

```java
TrackierEvent event = new TrackierEvent("sEMWSCTXeu");
event.param1 = "Param1";
TrackierSDK.trackEvent(event);
```

**Trigger:** When the app is launched (SplashActivity onCreate)

### 2. Game Result Events
**Location:** `app/src/main/java/com/cloudstuff/tictactoe/fragment/GameFragment.java`

#### Player One Win Event
```java
TrackierEvent event = new TrackierEvent("ErkEjPi4X1");
event.param1 = "Player one won";
TrackierSDK.trackEvent(event);
```

#### Player Two Win Event
```java
TrackierEvent event = new TrackierEvent("ErkEjPi4X1");
event.param1 = "Player two won";
TrackierSDK.trackEvent(event);
```

**Trigger:** When a player wins the game

### 3. Invite Friends Event
**Location:** `app/src/main/java/com/cloudstuff/tictactoe/fragment/SettingsFragment.java`

```java
TrackierEvent event = new TrackierEvent(TrackierEvent.INVITE);
event.param1 = "Settings Screen";
event.param2 = "Invite Button Clicked";
TrackierSDK.trackEvent(event);
```

**Trigger:** When user clicks "Invite Friends" button in settings

### 4. Uninstall Tracking

#### A. Firebase Analytics Integration
**Location:** `app/src/main/java/com/cloudstuff/tictactoe/activity/MainActivity.java`

```java
FirebaseAnalytics mFirebaseAnalytics;
mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
mFirebaseAnalytics.setUserProperty("ct_objectId", Objects.requireNonNull(TrackierSDK.getTrackierId()));
Log.d("TAG", "onCreate: "+TrackierSDK.getTrackierId());
```

**Trigger:** When the app is launched (MainActivity onCreate)
**Purpose:** Tracks uninstall events by setting Trackier ID as Firebase Analytics user property

#### B. FCM Token for Uninstall Tracking
**Location:** `app/src/main/java/com/cloudstuff/tictactoe/fcm/FCMFirebaseMessagingService.java`

```java
@Override
public void onNewToken(String token) {

    // Log the FCM token
    Log.d("FCM", "New FCM token: " + token);
    
    // Send the FCM token to TrackierSDK for uninstall tracking
    TrackierSDK.sendFcmToken(token);
}
```

**Trigger:** When FCM token is generated or refreshed
**Purpose:** Sends FCM token to Trackier SDK for push notification-based uninstall tracking

**Service Configuration:**
The service is registered in `AndroidManifest.xml`:
```xml
<service
    android:name="com.cloudstuff.tictactoe.fcm.FCMFirebaseMessagingService"
    android:directBootAware="true"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

## 🔗 Deep Link Handling

### Deep Link Listener
**Location:** `app/src/main/java/com/cloudstuff/tictactoe/TicTacToe.java`

Comprehensive logging for all deep link data:

```java
DeepLinkListener deepLinkListener = new DeepLinkListener() {
    public void onDeepLinking(@NonNull DeepLink deepLink) {
        Log.d("DeepLink", "=== Deep Link Received ===");
        Log.d("DeepLink", "Deep Link Value: " + deepLink.getDeepLinkValue());
        Log.d("DeepLink", "URL: " + deepLink.getUrl());
        Log.d("DeepLink", "Is Deferred: " + deepLink.isDeferred());
        Log.d("DeepLink", "Partner ID: " + deepLink.getPartnerId());
        Log.d("DeepLink", "Site ID: " + deepLink.getSiteId());
        Log.d("DeepLink", "Sub Site ID: " + deepLink.getSubSiteId());
        Log.d("DeepLink", "Campaign: " + deepLink.getCampaign());
        Log.d("DeepLink", "P1: " + deepLink.getP1());
        Log.d("DeepLink", "P2: " + deepLink.getP2());
        Log.d("DeepLink", "P3: " + deepLink.getP3());
        Log.d("DeepLink", "P4: " + deepLink.getP4());
        Log.d("DeepLink", "P5: " + deepLink.getP5());
        Log.d("DeepLink", "All Data: " + deepLink.getData());
        
        if (deepLink.getSdkParams() != null) {
            Log.d("DeepLink", "SDK Params: " + deepLink.getSdkParams());
        }
        
        Log.d("DeepLink", "=== End Deep Link Info ===");
    }
};
```

### Deep Link Parameter Extraction
**Location:** `app/src/main/java/com/cloudstuff/tictactoe/activity/MainActivity.java`

```java
private Map<String,String> getDeepLinkParams(Uri uri) {
    Map<String,String> deepLinkParams = new HashMap<String,String>();
    if(uri != null){
        Set<String> paramNames = uri.getQueryParameterNames();
        for(String name : paramNames){
           deepLinkParams.put(name,uri.getQueryParameter(name));
        }
    }
    return deepLinkParams;
}
```

## 🔄 Dynamic Link Creation

### Location: `app/src/main/java/com/cloudstuff/tictactoe/fragment/SettingsFragment.java`

Complete dynamic link creation with Trackier SDK:

```java
private void createDynamicLink(Context context) {
    // Build the dynamic link parameters
    Map<String, String> sdkParams = new HashMap<>();
    sdkParams.put("param1", "value1");
    sdkParams.put("param2", "value2");

    DynamicLink dynamicLink = new DynamicLink.Builder()
            .setTemplateId("78R2J2") // Set the template ID for the link
            .setLink(Uri.parse("https://trackier58.u9ilnk.me")) // The base link
            .setDomainUriPrefix("trackier59.unilink.me") // Domain prefix for the link
            .setDeepLinkValue("NewMainActivity") // Deep link destination within the app
            // Additional SDK parameters
            .setSDKParameters(sdkParams)
            // Attribution parameters for tracking
            .setAttributionParameters(
                    "my_channel",
                    "my_campaign",
                    "at_invite",
                    "param1_value",
                    "param2_value",
                    "param3_value",
                    "param4_value",
                    "param5_value"
            )
            .build();

    // Call the SDK to create the dynamic link
    TrackierSDK.createDynamicLink(
            dynamicLink,
            dynamicLinkUrl -> {
                // Log success messages
                Log.d("DynamicLinkSuccess", "Dynamic link created: " + dynamicLinkUrl);
                
                // Track the invite event
                TrackierEvent event = new TrackierEvent(TrackierEvent.INVITE);
                event.param1 = "Settings Screen";
                event.param2 = "Invite Button Clicked";
                TrackierSDK.trackEvent(event);
                
                // Share the dynamic link
                shareDynamicLink(dynamicLinkUrl);
                return null;
            },
            errorMessage -> {
                // Log error messages
                Log.d("DynamicLinkError", errorMessage);
                showError("Failed to create invite link. Please try again.");
                return null;
            }
    );
}
```

### Share Dynamic Link Function
```java
private void shareDynamicLink(String dynamicLinkUrl) {
    try {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Invite to Tic Tac Toe");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this awesome Tic Tac Toe game: " + dynamicLinkUrl);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    } catch (Exception e) {
        Log.e("ShareError", "Error sharing dynamic link: " + e.getMessage());
        showError("Failed to share invite link.");
    }
}
```

## 🔍 Deferred Deep Link Resolution for Instant Link

### Location: `app/src/main/java/com/cloudstuff/tictactoe/activity/MainActivity.java`

```java
// Resolve and print the deferred deep link URL
TrackierSDK.resolveDeeplinkUrl("https://trackier58.u9ilnk.me/d/NKmWH9E7b1", 
    resultUrl -> {
        Log.d("TrackierSDK", "Resolved Deferred Deep Link URL: " + resultUrl);
        return null;
    },
    error -> {
        Log.e("TrackierSDK", "Error resolving deferred deep link: " + error.getMessage());
        return null;
    }
);
```

## 📁 File Locations

### Core SDK Files:
- **Application Class:** `app/src/main/java/com/cloudstuff/tictactoe/TicTacToe.java`
- **Main Activity:** `app/src/main/java/com/cloudstuff/tictactoe/activity/MainActivity.java`
- **Splash Activity:** `app/src/main/java/com/cloudstuff/tictactoe/activity/SplashActivity.java`
- **Game Fragment:** `app/src/main/java/com/cloudstuff/tictactoe/fragment/GameFragment.java`
- **Settings Fragment:** `app/src/main/java/com/cloudstuff/tictactoe/fragment/SettingsFragment.java`
- **FCM Service:** `app/src/main/java/com/cloudstuff/tictactoe/fcm/FCMFirebaseMessagingService.java`

### Configuration Files:
- **Build Configuration:** `app/build.gradle`
- **Manifest:** `app/src/main/AndroidManifest.xml`
- **Proguard Rules:** `app/proguard-rules.pro`

## ⚙️ Configuration

### Dependencies (app/build.gradle):
```gradle
implementation 'com.trackier:android-sdk:1.6.73'
```

### Proguard Rules (app/proguard-rules.pro):
```proguard
-keep class com.trackier.sdk.** { *; }
```

### SDK Key Configuration:
```java
private static final String TR_DEV_KEY = "<PLACE_SDK_OR_APP_KEY_HERE>";
```

## 📝 Logging and Debugging

### Log Tags Used:
- `"DeepLink"` - Deep link data logging
- `"TrackierSDK"` - SDK operations logging
- `"DynamicLinkSuccess"` - Dynamic link creation success
- `"DynamicLinkError"` - Dynamic link creation errors
- `"ShareError"` - Share functionality errors
- `"FCM"` - FCM token generation and updates

### Key Log Messages:
1. **Deep Link Received:** Complete deep link data dump
2. **Resolved Deferred Deep Link:** URL resolution results
3. **Dynamic Link Created:** Successfully created dynamic links
4. **Event Tracking:** All tracked events with parameters
5. **FCM Token:** New and refreshed FCM tokens sent to Trackier SDK

## 🎯 Event Summary

| Event ID | Event Name | Location | Trigger |
|----------|------------|----------|---------|
| `sEMWSCTXeu` | App Launch | SplashActivity | App opened |
| `ErkEjPi4X1` | Game Win | GameFragment | Player wins game |
| `TrackierEvent.INVITE` | Invite Friends | SettingsFragment | Invite button clicked |
| `TrackierSDK.getTrackierId()` | Uninstall Tracking | MainActivity | App launched (Firebase Analytics) |
| `TrackierSDK.sendFcmToken()` | FCM Token for Uninstall | FCMFirebaseMessagingService | FCM token generated/refreshed |

## 🔧 Key Features Implemented

1. **✅ SDK Initialization** - Complete setup with deep link listener
2. **✅ Event Tracking** - Multiple events across different app sections
3. **✅ Deep Link Handling** - Comprehensive deep link data logging
4. **✅ Dynamic Link Creation** - Full invite functionality with sharing
5. **✅ Deferred Deep Link Resolution** - URL resolution with error handling
6. **✅ Error Handling** - Proper error logging and user feedback
7. **✅ Logging** - Detailed logging for debugging and monitoring
8. **✅ Uninstall Tracking** - Dual approach:
   - Firebase Analytics integration with Trackier ID
   - FCM token sent to Trackier SDK for push notification-based uninstall tracking
9. **✅ Push Notification Support** - FCM service integrated with Trackier SDK

## 🚀 Usage Instructions

1. **Replace SDK Key:** Update `TR_DEV_KEY` in `TicTacToe.java`
2. **Configure Template ID:** Update template ID in dynamic link creation
3. **Test Deep Links:** Use the provided test URL for deferred deep link testing
4. **Monitor Logs:** Use Logcat with filter tags to monitor SDK operations

## 📱 User Flow

1. **App Launch** → Trackier event fired + Uninstall tracking setup (Firebase Analytics + FCM)
2. **FCM Token Generation** → Token sent to Trackier SDK for uninstall tracking
3. **Game Play** → Win events tracked
4. **Invite Friends** → Dynamic link created and shared
5. **Deep Link Received** → Complete data logged
6. **Deferred Deep Link** → URL resolved and logged
7. **Uninstall Detection** → Dual tracking via Trackier ID (Firebase Analytics) and FCM token

## 🔔 FCM Configuration

### Firebase Messaging Service
The `FCMFirebaseMessagingService` provides:
- **Automatic token refresh handling**
- **Token sent to Trackier SDK** via `TrackierSDK.sendFcmToken(token)`
- **Push notification receiving** for game invites and other notifications
- **Uninstall tracking support** through FCM tokens

### How Uninstall Tracking Works
1. **On app install/launch**: FCM token is generated and sent to Trackier SDK
2. **Token refresh**: Whenever FCM token changes, it's automatically sent to Trackier
3. **Uninstall detection**: Trackier uses this token to detect when the app is uninstalled by attempting to send silent push notifications
4. **Dual tracking**: Combined with Firebase Analytics user property for comprehensive uninstall tracking

This integration provides comprehensive tracking, deep linking, dynamic link functionality, and uninstall tracking for the TicTacToe Android application using the Trackier SDK. 