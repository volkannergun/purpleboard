import os
import pathlib

# --- Configuration ---
# This is the name of your app module, which will become the main package name part.
# e.g., if your package is com.example.purpleboard, app_module_name is "purpleboard"
APP_MODULE_NAME = "purpleboard"
BASE_PACKAGE_PATH = f"com/example/{APP_MODULE_NAME}" # Used for package declarations in .kt files

# --- Helper Functions ---
def create_dir(path):
    """Creates a directory if it doesn't exist."""
    pathlib.Path(path).mkdir(parents=True, exist_ok=True)
    print(f"Ensured directory: {path}")

def create_file(path, content=""):
    """Creates a file with optional content if it doesn't exist."""
    if not pathlib.Path(path).exists():
        with open(path, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"Created file: {path}")
    else:
        print(f"File already exists (skipped): {path}")

def get_kt_file_content(package_suffix, class_name, class_type="class"):
    """Generates basic Kotlin file content with package and class definition."""
    full_package = BASE_PACKAGE_PATH.replace('/', '.')
    if package_suffix:
        full_package += f".{package_suffix}"
    
    # Handle cases where class_name might already imply its type (e.g., "InterfaceName")
    if class_type == "interface" and not class_name.endswith("Service"): # ApiService is a common case
        name_to_use = class_name
    elif class_type == "object":
        name_to_use = class_name
    else:
        name_to_use = class_name

    # Special handling for data classes or specific types
    if "Request" in class_name or "Response" in class_name or class_name in ["User", "Discussion", "Reply", "Assignment", "LeaderboardEntry", "ShopItem"]:
        class_type_keyword = "data class"
    elif class_type == "interface":
        class_type_keyword = "interface"
    elif class_type == "object":
        class_type_keyword = "object"
    else:
        class_type_keyword = "class"


    return f"""package {full_package}

{class_type_keyword} {name_to_use} {{
    // TODO: Implement {name_to_use}
}}
"""

def get_xml_layout_content(filename=""):
    """Generates basic XML layout content."""
    # Basic FrameLayout or LinearLayout to make it valid
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
</resources>
"""
    elif filename == "dimens.xml":
        return """<resources>
    <!-- Example Dimens -->
    <dimen name="fab_margin">16dp</dimen>
    <dimen name="activity_horizontal_margin">16dp</dimen>
    <dimen name="activity_vertical_margin">16dp</dimen>
    <dimen name="nav_header_vertical_spacing">8dp</dimen>
    <dimen name="nav_header_height">176dp</dimen>
</resources>
"""
    elif filename == "themes.xml": # For Material3
        return f"""<resources xmlns:tools="http://schemas.android.com/tools">
  <!-- Base application theme. -->
  <style name="Base.Theme.{APP_MODULE_NAME.capitalize()}" parent="Theme.Material3.DayNight.NoActionBar">
    <!-- Customize your light theme here. -->
    <!-- <item name="colorPrimary">@color/my_light_primary</item> -->
    <item name="colorPrimary">@color/app_purple_primary</item>
    <item name="colorPrimaryVariant">@color/app_purple_dark</item>
    <item name="colorOnPrimary">@color/white</item>
    <!-- Secondary brand color. -->
    <item name="colorSecondary">@color/teal_200</item>
    <item name="colorSecondaryVariant">@color/teal_700</item>
    <item name="colorOnSecondary">@color/black</item>
    <!-- Status bar color. -->
    <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
  </style>

  <style name="Theme.{APP_MODULE_NAME.capitalize()}" parent="Base.Theme.{APP_MODULE_NAME.capitalize()}" />

   <!-- Dark Theme (Optional - can be same as light or customized) -->
    <style name="Base.Theme.{APP_MODULE_NAME.capitalize()}.Dark" parent="Theme.Material3.Dark.NoActionBar">
        <!-- Customize your dark theme here. -->
        <!-- <item name="colorPrimary">@color/my_dark_primary</item> -->
        <item name="colorPrimary">@color/app_purple_dark</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/black</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_200</item> <!-- Adjusted for dark -->
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
    </style>
    <style name="Theme.{APP_MODULE_NAME.capitalize()}.Dark" parent="Base.Theme.{APP_MODULE_NAME.capitalize()}.Dark" />
</resources>
"""
    return "<!-- TODO -->"


def get_drawable_xml_content(filename):
    if filename == "bg_button_purple.xml":
        return """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/app_purple_primary"/>
    <corners android:radius="8dp"/>
</shape>
"""
    elif filename == "bg_completed_assignment.xml":
        return """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/app_green_completed"/>
    <corners android:radius="4dp"/>
</shape>
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
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
  <path
      android:fillColor="@android:color/white"
      android:pathData="M12,12c2.21,0 4,-1.79 4,-4s-1.79,-4 -4,-4 -4,1.79 -4,4 1.79,4 4,4zM12,14c-2.67,0 -8,1.34 -8,4v2h16v-2c0,-2.66 -5.33,-4 -8,-4z"/>
</vector>
"""
    return "<!-- TODO -->"


def get_nav_graph_content():
    return f"""<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/loginActivity">

    <!-- TODO: Define your navigation destinations and actions -->
    <!-- Example:
    <activity
        android:id="@+id/loginActivity"
        android:name=".{APP_MODULE_NAME}.activities.LoginActivity"
        android:label="activity_login"
        tools:layout="@layout/activity_login" />
    -->
</navigation>
"""

# --- Main Script Logic ---
def main():
    # --- Kotlin Source Directory Structure ---
    # Assumes script is run from .../java/com/example/
    # So, APP_MODULE_NAME will be created in the current directory.
    kotlin_base_dir = pathlib.Path(APP_MODULE_NAME)
    create_dir(kotlin_base_dir)

    kotlin_dirs = {
        "activities": [
            "LoginActivity", "RegisterActivity", "HomeActivity",
            "DiscussionsActivity", "NewDiscussionActivity", "DiscussionDetailActivity",
            "AssignmentsActivity", "AssignmentDetailActivity", "LeaderboardActivity",
            "ProfileActivity", "PointShopActivity"
        ],
        "adapters": [
            "DiscussionAdapter", "ReplyAdapter", "AssignmentAdapter",
            "LeaderboardAdapter", "AvatarAdapter", "ProfileDiscussionAdapter",
            "BadgeAdapter", "ShopItemAdapter"
        ],
        "fragments": ["AvatarPickerDialogFragment"],
        "models": [
            "User", "Discussion", "Reply", "Assignment", "LeaderboardEntry", "ShopItem",
            "ApiResponse", "LoginRequest", "RegisterRequest", "NewDiscussionRequest",
            "NewReplyRequest", "SubmitAssignmentRequest"
        ],
        "network": ["ApiService", "RetrofitClient"],
        "utils": ["Constants", "SharedPreferencesHelper", "Validators", "ViewExtensions"],
        "viewmodels": [ # Using specific ViewModels as they often have distinct roles
            "LoginViewModel", "RegisterViewModel", "HomeViewModel", "DiscussionsViewModel",
            "NewDiscussionViewModel", "DiscussionDetailViewModel", "AssignmentsViewModel",
            "AssignmentDetailViewModel", "LeaderboardViewModel", "ProfileViewModel", "PointShopViewModel"
        ]
    }

    for dir_name, files in kotlin_dirs.items():
        current_path = kotlin_base_dir / dir_name
        create_dir(current_path)
        for file_name in files:
            content_type = "class" # default
            if dir_name == "network" and file_name == "ApiService":
                content_type = "interface"
            if dir_name == "network" and file_name == "RetrofitClient":
                content_type = "object"
            if dir_name == "utils" and (file_name == "Constants" or file_name == "Validators" or file_name == "ViewExtensions"):
                content_type = "object" # Or keep as class if preferred for extensions
            
            create_file(
                current_path / f"{file_name}.kt",
                get_kt_file_content(dir_name, file_name, content_type)
            )
    
    # --- Resource Directory Structure ---
    # Relative path from .../java/com/example/ to .../res/
    res_base_dir = pathlib.Path("../../../../res")
    create_dir(res_base_dir)

    # Drawable directories
    drawable_dir = res_base_dir / "drawable"
    create_dir(drawable_dir)
    create_dir(drawable_dir / "avatars") # For avatar1.png etc.
    create_dir(drawable_dir / "icons")

    drawable_files = ["bg_button_purple.xml", "bg_completed_assignment.xml"]
    for f_name in drawable_files:
        create_file(drawable_dir / f_name, get_drawable_xml_content(f_name))

    icon_files = ["ic_crown.xml", "ic_profile_placeholder.xml"] # User will add actual vectors
    for f_name in icon_files:
        create_file(drawable_dir / "icons" / f_name, get_drawable_xml_content(f_name))
    print("Note: For .png avatars (avatar1.png ... avatar20.png), please add them manually to res/drawable/avatars/")


    # Layout directory
    layout_dir = res_base_dir / "layout"
    create_dir(layout_dir)
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
        create_file(layout_dir / f_name, get_xml_layout_content(f_name))

    # Values directory
    values_dir = res_base_dir / "values"
    create_dir(values_dir)
    values_files = ["colors.xml", "strings.xml", "dimens.xml", "themes.xml"]
    for f_name in values_files:
        create_file(values_dir / f_name, get_values_xml_content(f_name))
    
    # Create night-mode themes.xml for completeness
    values_night_dir = res_base_dir / "values-night"
    create_dir(values_night_dir)
    create_file(values_night_dir / "themes.xml", get_values_xml_content("themes.xml").replace("DayNight", "Dark").replace("my_light_primary", "my_dark_primary"))


    # Navigation directory
    navigation_dir = res_base_dir / "navigation"
    create_dir(navigation_dir)
    create_file(navigation_dir / "nav_graph.xml", get_nav_graph_content())

    # Mipmap directories (typically for app icons, create them empty)
    mipmap_dirs = [
        "mipmap-anydpi-v26", "mipmap-hdpi", "mipmap-mdpi",
        "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi"
    ]
    for m_dir in mipmap_dirs:
        create_dir(res_base_dir / m_dir)
        # Often an ic_launcher.xml or ic_launcher_round.xml is in anydpi-v26
        if m_dir == "mipmap-anydpi-v26":
             create_file(res_base_dir / m_dir / "ic_launcher.xml", "<!-- TODO: App launcher icon -->")
             create_file(res_base_dir / m_dir / "ic_launcher_round.xml", "<!-- TODO: App launcher icon round -->")


    print("\n--- Project structure creation complete ---")
    print(f"Base Kotlin package: {BASE_PACKAGE_PATH.replace('/', '.')}")
    print(f"Run Android Studio's 'Sync Project with Gradle Files' if it doesn't pick up changes automatically.")
    print("Remember to add dependencies to your build.gradle files (e.g., Retrofit, Gson, Material Components, etc.).")

if __name__ == "__main__":
    main()