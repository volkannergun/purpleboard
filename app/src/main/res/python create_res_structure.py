import os
import pathlib

# --- Configuration ---
# This is the name of your app module, used in themes.xml and strings.xml
APP_MODULE_NAME = "purpleboard"

# --- Helper Functions ---
def create_dir(path_str):
    """Creates a directory if it doesn't exist, relative to the script's location."""
    path = pathlib.Path(path_str)
    path.mkdir(parents=True, exist_ok=True)
    print(f"Ensured directory: {path.resolve()}")

def create_file(path_str, content=""):
    """Creates a file with optional content if it doesn't exist, relative to the script's location."""
    path = pathlib.Path(path_str)
    if not path.exists():
        with open(path, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"Created file: {path.resolve()}")
    else:
        print(f"File already exists (skipped): {path.resolve()}")

def get_xml_layout_content(filename=""):
    """Generates basic XML layout content."""
    view_group = "androidx.constraintlayout.widget.ConstraintLayout"
    if "item_" in filename or "dialog_" in filename:
        view_group = "LinearLayout" # Simpler for items initially

    return f"""<?xml version="1.0" encoding="utf-8"?>
<{view_group} xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- TODO: Implement layout for {filename} -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello from {filename}"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</{view_group}>
"""

def get_values_xml_content(filename):
    if filename == "colors.xml":
        return """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>

    <!-- App Specific Colors -->
    <color name="app_purple_primary">#6A1B9A</color> <!-- Example Purple -->
    <color name="app_purple_dark">#4A148C</color>
    <color name="app_gray_medium">#BDBDBD</color>
    <color name="app_gray_light">#EEEEEE</color>
    <color name="app_gray_dark_text">#424242</color>
    <color name="app_red_error">#B00020</color>
    <color name="app_green_completed">#00C853</color>
</resources>
"""
    elif filename == "strings.xml":
        return f"""<resources>
    <string name="app_name">{APP_MODULE_NAME.capitalize()}</string>
    <!-- Add other strings here -->
    <string name="login_title">Login</string>
    <string name="register_title">Registration</string>
    <string name="home_title">Home</string>
    <string name="discussions_title">Discussions</string>
    <string name="new_discussion_title">New Discussion</string>
    <string name="assignments_title">Assignments</string>
    <string name="leaderboards_title">Leaderboards</string>
    <string name="profile_title">Profile</string>
    <string name="point_shop_title">Point Shop</string>

    <string name="name_hint">Name</string>
    <string name="surname_hint">Surname</string>
    <string name="email_hint">Email</string>
    <string name="password_hint">Password</string>
    <string name="confirm_password_hint">Confirm Password</string>
    <string name="login_button">Login</string>
    <string name="go_to_register_button">Don\'t have an account? Register</string>
    <string name="register_button">Register</string>
    <string name="password_rules">Password must be 6-12 characters, including lowercase, uppercase, and a number.</string>
    <string name="password_mismatch">Passwords do not match.</string>

    <string name="discussions_page_button">Discussions</string>
    <string name="assignments_page_button">Assignments</string>
    <string name="leaderboards_page_button">Leaderboards</string>
    <string name="profile_page_button">My Profile</string>

    <string name="create_discussion_button">Create Discussion</string>
    <string name="topic_hint">Topic</string>
    <string name="description_hint">Description</string>
    <string name="create_button">Create</string>
    <string name="enter_reply_hint">Enter your reply...</string>
    <string name="send_reply_button">Send</string>
    <string name="complete_assignment_button">Send Reply & Complete</string>

    <string name="total_points_leaderboard">Total Points</string>
    <string name="current_points_leaderboard">Current Points</string>

    <string name="my_points_label">My Points: %d</string>
    <string name="point_shop_button">Point Shop</string>
    <string name="chatgpt_pro_code_button_text_default">30 Days ChatGPT Pro Code</string>
    <string name="chatgpt_pro_code_button_points_default">5 points</string>
    <string name="chatgpt_pro_code_revealed">N074-R34L-C0D3</string>

    <string name="avatar_picker_title">Choose Avatar</string>
    <string name="err_failed_to_load_data">Failed to load data. Please try again.</string>
    <string name="err_network_error">Network error. Please check your connection.</string>
    <string name="err_unknown">An unknown error occurred.</string>
    <string name="err_field_required">%s is required.</string>
    <string name="err_email_invalid">Invalid email format.</string>
    <string name="loading">Loading...</string>
    <string name="logout_button">Logout</string>


</resources>
"""
    elif filename == "dimens.xml":
        return """<resources>
    <!-- Default screen margins, per the Android Design guidelines. -->
    <dimen name="activity_horizontal_margin">16dp</dimen>
    <dimen name="activity_vertical_margin">16dp</dimen>
    <dimen name="fab_margin">16dp</dimen>

    <!-- General spacing -->
    <dimen name="spacing_small">4dp</dimen>
    <dimen name="spacing_medium">8dp</dimen>
    <dimen name="spacing_large">16dp</dimen>
    <dimen name="spacing_xlarge">24dp</dimen>
    <dimen name="spacing_xxlarge">32dp</dimen>

    <!-- Text Sizes -->
    <dimen name="text_size_small">12sp</dimen>
    <dimen name="text_size_medium">14sp</dimen>
    <dimen name="text_size_large">16sp</dimen>
    <dimen name="text_size_xlarge">20sp</dimen>
    <dimen name="text_size_title">24sp</dimen>
    <dimen name="text_size_header">34sp</dimen>

    <!-- Button sizes -->
    <dimen name="button_height_standard">48dp</dimen>
    <dimen name="button_corner_radius">8dp</dimen>

    <!-- Avatar sizes -->
    <dimen name="avatar_size_small">40dp</dimen>
    <dimen name="avatar_size_medium">60dp</dimen>
    <dimen name="avatar_size_large">80dp</dimen>
    <dimen name="avatar_size_profile">100dp</dimen>

    <!-- Leaderboard -->
    <dimen name="leaderboard_item_height">60dp</dimen>
    <dimen name="leaderboard_top_item_extra_margin_vertical">20dp</dimen> <!-- (60*3 - 60) / 2, if top is 3x height of others (60dp * 3 = 180dp total height, so 60dp margin top/bottom) -->
                                                                        <!-- Or simply, normal_margin + (normal_height*2 / 2) -->
    <dimen name="leaderboard_crown_size">24dp</dimen>

    <!-- Card elevation and corner radius -->
    <dimen name="card_corner_radius">8dp</dimen>
    <dimen name="card_elevation">4dp</dimen>
</resources>
"""
    elif filename == "themes.xml": # For Material3
        return f"""<resources xmlns:tools="http://schemas.android.com/tools">
  <!-- Base application theme. -->
  <style name="Base.Theme.{APP_MODULE_NAME.capitalize()}" parent="Theme.Material3.DayNight.NoActionBar">
    <!-- Customize your light theme here. -->
    <item name="colorPrimary">@color/app_purple_primary</item>
    <item name="colorPrimaryVariant">@color/app_purple_dark</item> <!-- Used for status bar with NoActionBar -->
    <item name="colorOnPrimary">@color/white</item>
    <!-- Secondary brand color. -->
    <item name="colorSecondary">@color/teal_200</item> <!-- You can choose another purple or accent -->
    <item name="colorSecondaryVariant">@color/teal_700</item>
    <item name="colorOnSecondary">@color/black</item>
    <!-- Status bar color. -->
    <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
    <!-- Customize other attributes -->
    <item name="android:windowBackground">@color/app_gray_light</item>
    <item name="android:textColor">@color/app_gray_dark_text</item>
  </style>

  <style name="Theme.{APP_MODULE_NAME.capitalize()}" parent="Base.Theme.{APP_MODULE_NAME.capitalize()}">
        <!-- Screen specific action bar -->
        <!-- <item name="windowActionBar">false</item> -->
        <!-- <item name="windowNoTitle">true</item> -->
  </style>

  <!-- Specific style for Activities that DO have an ActionBar -->
  <style name="Theme.{APP_MODULE_NAME.capitalize()}.WithActionBar" parent="Theme.Material3.DayNight">
    <item name="colorPrimary">@color/app_purple_primary</item>
    <item name="colorPrimaryVariant">@color/app_purple_dark</item>
    <item name="colorOnPrimary">@color/white</item>
    <item name="colorSecondary">@color/teal_200</item>
    <item name="colorSecondaryVariant">@color/teal_700</item>
    <item name="colorOnSecondary">@color/black</item>
    <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
    <item name="android:windowBackground">@color/app_gray_light</item>
    <item name="android:textColor">@color/app_gray_dark_text</item>
  </style>

  <!-- TextInputLayout Style -->
    <style name="Widget.Purpleboard.TextInputLayout" parent="Widget.MaterialComponents.TextInputLayout.OutlinedBox">
        <item name="boxStrokeColor">@color/text_input_layout_stroke_color</item>
        <item name="hintTextColor">?attr/colorPrimary</item>
    </style>

    <!-- Button Style -->
    <style name="Widget.Purpleboard.Button" parent="Widget.MaterialComponents.Button">
        <item name="android:backgroundTint">@color/app_purple_primary</item>
        <item name="android:textColor">@color/white</item>
        <item name="cornerRadius">@dimen/button_corner_radius</item>
        <item name="android:paddingTop">12dp</item>
        <item name="android:paddingBottom">12dp</item>
    </style>

    <style name="Widget.Purpleboard.Button.TextButton" parent="Widget.MaterialComponents.Button.TextButton">
        <item name="android:textColor">?attr/colorPrimary</item>
    </style>

    <style name="Widget.Purpleboard.CardView" parent="Widget.MaterialComponents.CardView">
        <item name="cardCornerRadius">@dimen/card_corner_radius</item>
        <item name="cardElevation">@dimen/card_elevation</item>
        <item name="android:layout_margin">@dimen/spacing_medium</item>
    </style>

</resources>
"""
    return "<!-- TODO -->"

def get_text_input_stroke_color_selector():
    return """<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="?attr/colorPrimary" android:state_focused="true"/>
    <item android:color="@color/app_gray_medium" android:state_hovered="true"/>
    <item android:color="@color/app_gray_medium" android:state_enabled="false"/>
    <item android:color="@color/app_gray_medium"/> <!-- Default color -->
</selector>
"""


def get_drawable_xml_content(filename):
    if filename == "bg_button_purple.xml":
        return """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/app_purple_primary"/>
    <corners android:radius="@dimen/button_corner_radius"/>
</shape>
"""
    elif filename == "bg_completed_assignment.xml":
        return """<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/app_green_completed"/>
            <corners android:radius="@dimen/card_corner_radius"/>
        </shape>
    </item>
</layer-list>
"""
    elif filename == "bg_assignment_item.xml": # Default background for assignment item
        return """<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/white"/> <!-- Or another default item color -->
            <corners android:radius="@dimen/card_corner_radius"/>
            <stroke android:width="1dp" android:color="@color/app_gray_medium"/>
        </shape>
    </item>
</layer-list>
"""
    elif filename == "ic_crown.xml": # Placeholder Vector
        return """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
  <path
      android:fillColor="@android:color/white"
      android:pathData="M5,16L3,5L8.5,12L12,5L15.5,12L21,5L19,16H5M19,19C19,19.55 18.55,20 18,20H6C5.45,20 5,19.55 5,19V18H19V19Z"/>
</vector>
"""
    elif filename == "ic_profile_placeholder.xml":
        return """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="96dp"
    android:height="96dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="@color/app_gray_medium">
  <path
      android:fillColor="@android:color/white"
      android:pathData="M12,12c2.21,0 4,-1.79 4,-4s-1.79,-4 -4,-4 -4,1.79 -4,4 1.79,4 4,4zM12,14c-2.67,0 -8,1.34 -8,4v2h16v-2c0,-2.66 -5.33,-4 -8,-4z"/>
</vector>
"""
    # Add other icons if needed, e.g. for home page buttons
    elif filename == "ic_discussions.xml": # Placeholder
        return """<vector xmlns:android="http://schemas.android.com/apk/res/android" android:height="24dp" android:tint="#000000" android:viewportHeight="24" android:viewportWidth="24" android:width="24dp"><path android:fillColor="@android:color/white" android:pathData="M20,2L4,2c-1.1,0 -1.99,0.9 -1.99,2L2,22l4,-4h14c1.1,0 2,-0.9 2,-2L22,4c0,-1.1 -0.9,-2 -2,-2zM6,9h12v2L6,11L6,9zM14,14L6,14v-2h8v2zM18,8L6,8L6,6h12v2z"/></vector>"""
    elif filename == "ic_assignments.xml": # Placeholder
        return """<vector xmlns:android="http://schemas.android.com/apk/res/android" android:height="24dp" android:tint="#000000" android:viewportHeight="24" android:viewportWidth="24" android:width="24dp"><path android:fillColor="@android:color/white" android:pathData="M19,3h-4.18C14.4,1.84 13.3,1 12,1s-2.4,0.84 -2.82,2L5,3c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2L21,5c0,-1.1 -0.9,-2 -2,-2zM12,3c0.55,0 1,0.45 1,1s-0.45,1 -1,1 -1,-0.45 -1,-1 0.45,-1 1,-1zM19,19L5,19L5,5h14v14zM12,6l-2,4h1.5v4L14,10h-1.5L12.5,6z"/></vector>""" # This is a flash_on icon, good placeholder for assignments
    elif filename == "ic_leaderboard.xml": # Placeholder
        return """<vector xmlns:android="http://schemas.android.com/apk/res/android" android:height="24dp" android:tint="#000000" android:viewportHeight="24" android:viewportWidth="24" android:width="24dp"><path android:fillColor="@android:color/white" android:pathData="M16,11V3L12.5,4.5L9,3V11H16ZM7,11.67C7,11.41 7.07,11.15 7.22,10.93L10,7L7,4L3,6V12L5,11L7,12.5V21H18V13.83C18,13.57 17.93,13.31 17.78,13.09L15,9L18,6L21,8V17L19,18L17,16.5V11.67H7Z"/></vector>""" # This is a emoji_events icon
    elif filename == "ic_profile.xml": # Placeholder
        return """<vector xmlns:android="http://schemas.android.com/apk/res/android" android:height="24dp" android:tint="#000000" android:viewportHeight="24" android:viewportWidth="24" android:width="24dp"><path android:fillColor="@android:color/white" android:pathData="M12,12c2.21,0 4,-1.79 4,-4s-1.79,-4 -4,-4 -4,1.79 -4,4 1.79,4 4,4zM12,14c-2.67,0 -8,1.34 -8,4v2h16v-2c0,-2.66 -5.33,-4 -8,-4z"/></vector>"""
    elif filename == "ic_point_shop.xml": # Placeholder
        return """<vector xmlns:android="http://schemas.android.com/apk/res/android" android:height="24dp" android:tint="#000000" android:viewportHeight="24" android:viewportWidth="24" android:width="24dp"><path android:fillColor="@android:color/white" android:pathData="M18,4L6,4c-1.1,0 -2,0.9 -2,2v12c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2L20,6c0,-1.1 -0.9,-2 -2,-2zM6,6h12v2L6,8L6,6zM12,16c-1.66,0 -3,-1.34 -3,-3s1.34,-3 3,-3 3,1.34 3,3 -1.34,3 -3,3zM18,12L6,12v-2h12v2z"/></vector>""" # This is a shopping_bag icon
    elif filename == "ic_logout.xml":
        return """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
  <path
      android:fillColor="@android:color/white"
      android:pathData="M17,7l-1.41,1.41L18.17,11H8v2h10.17l-2.58,2.58L17,17l5,-5zM4,5h8V3H4c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h8v-2H4V5z"/>
</vector>
"""

    return "<!-- TODO: Add your drawable resource -->"

def get_nav_graph_content():
    # Using activity names directly based on your previous plan.
    # Adjust "com.example.purpleboard" if your package name is different for the activities.
    # The script is run from /res, so the full path to activities is needed for tools:layout
    # However, for `android:name`, a relative path like ".activities.LoginActivity" is common if the manifest has the base package.
    # For full clarity in nav graph, using full package name.
    # The actual package name will be determined by your AndroidManifest.xml and build.gradle.
    # Let's assume the package name for activities will be com.coursemobile.purpleboard.activities
    # If you used com.example.purpleboard, replace "com.coursemobile.purpleboard" below
    # The APP_MODULE_NAME here is just "purpleboard"
    # So the activity package would be com.yourdomain.purpleboard.activities
    # For now, I'll use a placeholder for the full package name. You'll adjust this
    # when you actually implement navigation. The tools:layout uses relative paths from app root.
    activity_package_placeholder = f"com.coursemobile.{APP_MODULE_NAME}.activities"

    return f"""<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/loginActivity">

    <activity
        android:id="@+id/loginActivity"
        android:name="{activity_package_placeholder}.LoginActivity"
        android:label="activity_login"
        tools:layout="@layout/activity_login" >
        <action
            android:id="@+id/action_loginActivity_to_registerActivity"
            app:destination="@id/registerActivity" />
        <action
            android:id="@+id/action_loginActivity_to_homeActivity"
            app:destination="@id/homeActivity"
            app:popUpTo="@id/loginActivity"
            app:popUpToInclusive="true" />
    </activity>
    <activity
        android:id="@+id/registerActivity"
        android:name="{activity_package_placeholder}.RegisterActivity"
        android:label="activity_register"
        tools:layout="@layout/activity_register" >
        <action
            android:id="@+id/action_registerActivity_to_loginActivity"
            app:destination="@id/loginActivity"
            app:popUpTo="@id/loginActivity"
            app:popUpToInclusive="true"/>
    </activity>
    <activity
        android:id="@+id/homeActivity"
        android:name="{activity_package_placeholder}.HomeActivity"
        android:label="activity_home"
        tools:layout="@layout/activity_home" >
        <action
            android:id="@+id/action_homeActivity_to_discussionsActivity"
            app:destination="@id/discussionsActivity" />
        <action
            android:id="@+id/action_homeActivity_to_assignmentsActivity"
            app:destination="@id/assignmentsActivity" />
        <action
            android:id="@+id/action_homeActivity_to_leaderboardActivity"
            app:destination="@id/leaderboardActivity" />
        <action
            android:id="@+id/action_homeActivity_to_profileActivity"
            app:destination="@id/profileActivity" />
    </activity>
    <activity
        android:id="@+id/discussionsActivity"
        android:name="{activity_package_placeholder}.DiscussionsActivity"
        android:label="activity_discussions"
        tools:layout="@layout/activity_discussions" >
        <action
            android:id="@+id/action_discussionsActivity_to_newDiscussionActivity"
            app:destination="@id/newDiscussionActivity" />
        <action
            android:id="@+id/action_discussionsActivity_to_discussionDetailActivity"
            app:destination="@id/discussionDetailActivity" />
    </activity>
    <activity
        android:id="@+id/newDiscussionActivity"
        android:name="{activity_package_placeholder}.NewDiscussionActivity"
        android:label="activity_new_discussion"
        tools:layout="@layout/activity_new_discussion" />
    <activity
        android:id="@+id/discussionDetailActivity"
        android:name="{activity_package_placeholder}.DiscussionDetailActivity"
        android:label="activity_discussion_detail"
        tools:layout="@layout/activity_discussion_detail">
        <argument
            android:name="discussionId"
            app:argType="integer" />
    </activity>
    <activity
        android:id="@+id/assignmentsActivity"
        android:name="{activity_package_placeholder}.AssignmentsActivity"
        android:label="activity_assignments"
        tools:layout="@layout/activity_assignments" >
        <action
            android:id="@+id/action_assignmentsActivity_to_assignmentDetailActivity"
            app:destination="@id/assignmentDetailActivity" />
    </activity>
    <activity
        android:id="@+id/assignmentDetailActivity"
        android:name="{activity_package_placeholder}.AssignmentDetailActivity"
        android:label="activity_assignment_detail"
        tools:layout="@layout/activity_assignment_detail">
        <argument
            android:name="assignmentId"
            app:argType="integer" />
    </activity>
    <activity
        android:id="@+id/leaderboardActivity"
        android:name="{activity_package_placeholder}.LeaderboardActivity"
        android:label="activity_leaderboard"
        tools:layout="@layout/activity_leaderboard" />
    <activity
        android:id="@+id/profileActivity"
        android:name="{activity_package_placeholder}.ProfileActivity"
        android:label="activity_profile"
        tools:layout="@layout/activity_profile" >
        <action
            android:id="@+id/action_profileActivity_to_pointShopActivity"
            app:destination="@id/pointShopActivity" />
    </activity>
    <activity
        android:id="@+id/pointShopActivity"
        android:name="{activity_package_placeholder}.PointShopActivity"
        android:label="activity_point_shop"
        tools:layout="@layout/activity_point_shop" />

    <!-- Add other destinations (fragments, dialogs) if needed -->
    <dialog
        android:id="@+id/avatarPickerDialogFragment"
        android:name="{activity_package_placeholder.replace(".activities", ".fragments")}.AvatarPickerDialogFragment"
        android:label="dialog_avatar_picker"
        tools:layout="@layout/dialog_avatar_picker" />

</navigation>
"""


# --- Main Script Logic ---
def main():
    # Current directory is 'res'

    # Drawable directories
    create_dir("drawable")
    create_dir("drawable/avatars") # For avatar1.png etc.
    create_dir("drawable/icons")

    drawable_files = ["bg_button_purple.xml", "bg_completed_assignment.xml", "bg_assignment_item.xml"]
    for f_name in drawable_files:
        create_file(f"drawable/{f_name}", get_drawable_xml_content(f_name))

    icon_files = ["ic_crown.xml", "ic_profile_placeholder.xml", "ic_discussions.xml",
                  "ic_assignments.xml", "ic_leaderboard.xml", "ic_profile.xml", "ic_point_shop.xml", "ic_logout.xml"]
    for f_name in icon_files:
        create_file(f"drawable/icons/{f_name}", get_drawable_xml_content(f_name))
    print("Note: For .png avatars (avatar1.png ... avatar20.png), please add them manually to drawable/avatars/")

    # Layout directory
    create_dir("layout")
    layout_files = [
        "activity_login.xml", "activity_register.xml", "activity_home.xml",
        "activity_discussions.xml", "activity_new_discussion.xml", "activity_discussion_detail.xml",
        "activity_assignments.xml", "activity_assignment_detail.xml", "activity_leaderboard.xml",
        "activity_profile.xml", "activity_point_shop.xml",
        "item_discussion.xml", "item_reply.xml", "item_assignment.xml",
        "item_leaderboard_entry.xml", "item_leaderboard_entry_top.xml", "item_avatar.xml",
        "item_profile_discussion.xml", "item_badge.xml", "item_shop.xml",
        "dialog_avatar_picker.xml"
    ]
    for f_name in layout_files:
        create_file(f"layout/{f_name}", get_xml_layout_content(f_name))

    # Values directory
    create_dir("values")
    values_files = ["colors.xml", "strings.xml", "dimens.xml", "themes.xml"]
    for f_name in values_files:
        create_file(f"values/{f_name}", get_values_xml_content(f_name))
    
    # Color selector for TextInputLayout
    create_dir("color") # Android Studio usually puts color selectors in res/color/
    create_file("color/text_input_layout_stroke_color.xml", get_text_input_stroke_color_selector())


    # Create night-mode themes.xml for completeness
    create_dir("values-night")
    # We can reuse the themes.xml content, it handles DayNight.
    # Or provide a specific dark theme. For simplicity, let's use the same one,
    # as the Material3 DayNight theme will handle it.
    # If you want a fully custom dark theme, you'd modify the themes.xml content for the dark variant specifically.
    create_file("values-night/themes.xml", get_values_xml_content("themes.xml")) # Or a specific dark version

    # Navigation directory
    create_dir("navigation")
    create_file("navigation/nav_graph.xml", get_nav_graph_content())

    # Mipmap directories (typically for app icons, create them empty or with placeholders)
    mipmap_dirs = [
        "mipmap-anydpi-v26", "mipmap-hdpi", "mipmap-mdpi",
        "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi"
    ]
    for m_dir in mipmap_dirs:
        create_dir(m_dir)
        # Often an ic_launcher.xml or ic_launcher_round.xml is in anydpi-v26
        if m_dir == "mipmap-anydpi-v26":
             create_file(f"{m_dir}/ic_launcher.xml", "<!-- TODO: App launcher icon (vector) -->")
             create_file(f"{m_dir}/ic_launcher_round.xml", "<!-- TODO: App launcher icon round (vector) -->")
        else:
            # You would typically put PNG launcher icons here
            create_file(f"{m_dir}/ic_launcher.png", "<!-- TODO: App launcher icon (PNG) -->")
            create_file(f"{m_dir}/ic_launcher_round.png", "<!-- TODO: App launcher icon round (PNG) -->")


    print("\n--- Resource structure creation complete within 'res' directory ---")
    print(f"Run Android Studio's 'Sync Project with Gradle Files' if it doesn't pick up changes automatically.")
    print("Remember to add your actual image assets and refine XML values.")

if __name__ == "__main__":
    main()