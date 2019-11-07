package dk.sofushilfling.roomreservation


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        /*
            Prerequisites:
            1. User must be signed out
        */
        FirebaseAuth.getInstance().signOut()


        /*
            Test story:
            1. Start at MainActivity
            2. Navigate to a specific room
            3. Check to see if guest is able to make a reservation
            4. Go back to MainActivity
            5. Navigate to LoginActivity
            6. Try to login with a non valid email and password
            7. Try to login with non existing user
            8. Check that the non existing user is not being signed in
            9. Try to sign in with existing user
            10. Check that the existing user was signed in
            11. Navigate to a specific room
            12. Check to see if signed in user is able to make a new reservation
         */

        // 2. Navigate to a specific room
        onData(anything())
            .inAdapterView(withId(R.id.roomListView))
            .atPosition(0)
            .perform(click())

        // 3. Check to see if guest is able to make a reservation
        onView(withId(R.id.fab))
            .check(matches(not(isDisplayed())))

        // 4. Go back to MainActivity
        onView(allOf(withContentDescription("Navigate up"), childAtPosition(allOf(withId(R.id.toolbar_specific_room), childAtPosition(withClassName(`is`("android.widget.LinearLayout")),0)),0), isDisplayed()))
            .perform(click())

        // 5. Navigate to LoginActivity
        onView(allOf(withId(R.id.login_menu_item), withContentDescription("Sign in"), childAtPosition(childAtPosition(withId(R.id.toolbar),1),1)))
            .perform(click())

        // 6. Try to login with a non valid email and password
        val emailView = onView(withId(R.id.email))
        emailView.perform(replaceText("email"), closeSoftKeyboard())

        val passView = onView(withId(R.id.password))
        passView.perform(replaceText("pass"), closeSoftKeyboard())

        val signInViewBtn = onView(withId(R.id.login))
        signInViewBtn.check(matches(not(isEnabled())))

        onView(allOf(withId(R.id.register), childAtPosition(allOf(withId(R.id.container), childAtPosition(withId(android.R.id.content),0)),4), isDisplayed()))
            .check(matches(not(isEnabled())))

        // 7. Try to login with non existing user
        emailView.perform(replaceText("test@mail.com"))
        passView.perform(replaceText("password"))
        passView.perform(closeSoftKeyboard())

        //signInViewBtn.perform(click())

        // 8. Check that the non existing user is not being signed in
        // (if login is successful then the app will return to MainActivity)
        //intended(hasComponent(LoginActivity::class.java.name))


        // 9. Try to sign in with existing user
        emailView.perform(replaceText("sofu0185@edu.easj.dk"))
        passView.perform(replaceText("secret"))
        passView.perform(closeSoftKeyboard())

        //signInViewBtn.perform(click())

        // 10. Check that the existing user was signed in
        // (if login is successful then the app will return to MainActivity)
        //intended(hasComponent(MainActivity::class.java.name))

    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
