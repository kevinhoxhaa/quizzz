# Notes: week 8 (21.03 - 28.03)

Location: 		Drebbelweg  
Datum: 		    29-03-2022  
Time: 		    16:45 - 17:30
Attendees:		Alican Ekşi, Calin Georgescu, Boris Goranov, Kevin Hoxha, Varga Pál Patrik, Bink Boëtius, Nada Mouman  
Chair: 	        Varga Pál Patrik  
Notetaker:	    Boris Goranov

## Agenda items

### Opening by chair:

The meeting started in 16:45 as planned.

### Check-in:

> Stand up and inform each other on how thing are going.

Bink: almost done with the rematch functionality, only some minor bugs to fix left.

Alican: the "remove incorrect answer" joker is also almost complete, there are some infinite loops in the code that need to be identified and removed.

Boris: completed the emojis and activity image loading with Patrik and fixed some minor application bugs.

Calin: almost done with the half-time joker and completed the "kick after three unanswered questions" functionality.

Kevin: completed the double points joker and created a joker code template, which was then used to implement the other jokers.

Patrik: completed the emojis and activity image loading with Boris and fixed some minor application bugs.

### Discussion:

> Big-picture visual design choices

Boris: the chosen theme of the application will be pixelart and we have decided to make the application non-responsive, fixed in the center of the screen.

Kevin: a better solution would be to just fix the game window to a certain dimension instead of centering the components when the page is resized.

The other team members agreed with this approach and Boris took the task to implement the aforementioned design changes.

### Tips and Tops:

> Give feedback to each other or to the whole team.

### Patrik

**Top to Calin:** it is good that you started putting some effort into the development of the game, and took responsibility for the issues that you have been assigned.

**Tip to Calin:** you should try to work on your critical thinking skills and spend some time to understand the structure of the project, instead of asking questions immediately after you have started working on some functionality.

### Kevin

**Top to Group:** great teamwork from the whole team.

### Boris

**Top to Kevin:** great work finding bugs in the game. You are the only one who actually spent some time debugging the whole game cycle by impersonating the role of a user.

**Tip to Kevin:** although it is great to find bugs in the game, try to identify where the bug might come from, instead of just stating that there is a bug. It is true that the person who generated that bug may be more informed, but it would be much more of a constructive feedback if you show that you have actually spent some time thinking about what might cause the issue.

### Question round:

> Ask others or group TA any questions from the past week (final presentation, coding deadline, etc.).

Boris: there is an image in our activity repo that is in `webp` format, but is named as a JPEG. This is causing issues when trying to transfer the image between the server and the client. Should we create a merge request and resolve the issue on the activity repository?

Nada: there is no need to resolve this issue. Just create a default image that shows when there is an error.

Kevin: currently, we post the answer of a question by updating the `points` field of the user. Should we try to refactor this as a `PUT` request?

Nada: there is no need to refactor. The people responsible for grading the project only look at the ready product and whether the code has been well documented, structured and tested.

Patrik: can we use the thonk emoji as an error image?

Nada: it is ok to use the thonk emoji as the default error image.

### Feedback round:

> Ask group TA for feedback on the group progress.

Yellow flag on issue management: Alican, Calin and Bink are not updating their issues - some issues have been resolved but are not moved to doing, done, etc.

Yellow flag on continuous work and contribution: Alican and Calin could still contribute more

Green flag on style and code structure: `ServerUtils` can be divided, methods such as `useRemoveIncorrect()` is too complex.

Green flag on testing: the coverage of the `commons` package could still be improved.

Yellow flag on code reviews: Calin, Alican and Bink don't have enough reviews.

Green flag on pipelines: Calin and Alican should be more careful with the pipeline and run it locally before pushing anything.

The `README` file should be updated - describe and explain how to run the application.

Start with manual testing.

Next week, a video should be uploaded (deadline Friday); instructions on the video should be released later by the course staff. A draft of the video can be sent before this Sunday.

### Video:

Nada: the duration of the video is around 10 minutes, everyone should be present and has to show their faces. The audience is the client and they should be convinced that the product presented is unique and solves a relevant problem:

- Introduction: 15 - 30 seconds
- Brief overview of the process: 1 minute
- Showcase the application: live demo, about 8 minutes
- Conclusion and possible future improvements: about 30 seconds

The minimum time last year was 9 minutes and the maximum was 11 minutes (10% tolerance).

### Summary action points:

> Choose members responsible for the last meeting and clarify action points for next week.

Patrik: the video will be discussed on Thursday - a draft transcript could be given to Nada before this Sunday.

Boris: will take the `README` issue and complete it by the beginning of next week.

### Task distribution:

> Distribute the tasks for the last week of actual coding and choosing a live coding session time.

Done during the meeting on Thursday.

Boris: static application page design, connect ranking page to the game, components should be customised according to the project theme, text formatting using monospace and pixelart.

Alican: tableview redesign, dialog components should be standardised and have custom control buttons.

Bink: manual dialog should have a sub-title structure, emoji popup animations with Calin.

Patrik: class structure refactoring, remove games from game list object, refactor question text, redesign comparison question.

Kevin: button and emoji hover and click animations, streak functionality.

Calin: joker notifications, emoji popup animations with Bink, complete ongoing issues - admin panel and debug half-time joker.

### Closure:

Closing of the last official meeting of group 49 for the CSE1105 course.
