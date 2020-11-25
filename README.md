[Home](https://vanfleet0351.github.io/Kyle-Van-Fleet-Portfolio/)

# TutorMe Android App
TutorMe was developed in a 3 person team as part of a semester long class project in [CSE 5236](http://web.cse.ohio-state.edu/~champion.17/5236/). Our team was charged with designing and developing a mobile application that uniquely met a clear need in today’s market, was well-designed, and included the following: a UI, data that persists throughout multiple user sessions, one Internet-based service, and one device sensor.

### Collaborators
* [Andy Cui](https://github.com/acui97) (andrewlcui@gmail.com)
* [Robert Yost](https://github.com/RobertYost) (bobby.9202@gmail.com)

### Technology Used:
* Kotlin
* Android Studios
* [Glide](https://github.com/bumptech/glide)
* [Firebase](https://firebase.google.com)
* [CircleImageView](https://github.com/hdodenhof/CircleImageView)
* [Groupie RecyclerView](https://github.com/lisawray/groupie)
* [Android Jetpack](https://developer.android.com/jetpack)
* [Picasso](https://github.com/square/picasso)

![login verical](img/loginvertical.png)

### About the App
Our App helps tutors and students seeking a tutor services to easily find each other. With our app, tutors will no longer have to print and post fliers with their phone numbers all around campus. Students who are struggling with a class will no longer have to desperately search bulletin boards on campus, hoping to find a tutor that can help them. Tutors only need to create an account and list the subjects they are tutoring. Students only need to create and account and list the classes they need help with. 

After creating an account, a student will be presented with a group of tutors that are willing to help them. The student can swipe through the profiles of the tutors that are teaching the subject they want and select the one they like the most. A dialog will then be started to allow the student and tutor to set up a time, meeting place, and hourly rate they can agree upon.

The students can also use our app to meet with other students to form study groups. A student wishing to start a study group just needs to create a group on our app, and all other students searching for study groups in the same subject will have the option to join their study group.


### My Contributions

* **Messaging Feature:** The messaging feature in this app functions similar to how a typical text messaging application works. Users can send messages back and forth to each other with the most recent message being displayed at the bottom of a vertical list of messages. Messages that a users sends appear on the right hand side of the screen and messages that are recieved appear on the left hand side. Messages appear in realtime while the app refreshes in the background and the list of messages automatically scrolls to the bottom to show the newly arrived message. When the user clicks the text box to enter a new message, the list automatically scrolls to display the most recent messages. All messages are stored in a no-sql data via firebase.


![message vertical](img/messagevertical.png)![friends list](img/friendslist.png)

* **Profile Creation:** The profile creation view of the app includes profile picture selection, basic information entry, and school selection with recommendations based on gps location. The profile picture selection allows the user to select a perviously take photo on their phone or take a new one.

![profile](img/profile.png)

* **Background Threading:** Created general background threading to improve responsivness of the application.
* **View Design and Creation:** General design and creation of the different views seen such as the login view, message view, settings view, etc.

[Home](https://vanfleet0351.github.io/Kyle-Van-Fleet-Portfolio/)
