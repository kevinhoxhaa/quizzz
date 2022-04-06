## Introduction

This is a brief description of all the functionality that we expect from the client side of the app. If the user finds that everything in the app is in accordance with this document, that means that the app has passed manual testing.

## Visual design

On all screens...
- The visual design should revolve around pixel-art
- Texts should use either a pixel-art, or a monospace typeface
- There should be custom cursors, at least one for clickable and one for non-clickable surfaces

## Screen-specific features

In this section, we\'re describing how each of the screens should behave. An important requirement is that the screens should behave this way consistently, even after finishing/leaving a game, joining a new game, etc. arbitrarily many times.

On the given screen, the user should…

### Home screen

- Be able to enter a username and server address
- Be notified when either is invalid
- Be able to start a solo game
- Be able to join the waiting room for a multiplayer game
- Be able to open the admin screen

### Admin screen
- See the existing activities
- Be able to add a new activity
- Be able to edit an existing activity
- Be able to delete an activity
- Be able to refresh the list of activities
- Be able to return to the home screen

### Solo question screen
- See the text corresponding to the current question
- See an image corresponding to the current question
- See three options for answers
- See the number of the current question, starting at 1
- See the red/green/highlighted/grey circles, corresponding to each of the questions in the game
- See their current score, which should be the same as the score shown on the previous answer screen, or 0 for the first question
- Be able to change the color of the buttons by hovering over them
- Be able to select an answer and have its color changed
- See a timer that is counting down to zero, after which the user will be redirected to the answer page
- Be able to leave the game, after confirming that's what they want to do

### Solo estimation screen
- See the text corresponding to the current question
- See an image corresponding to the current question
- See the number of the current question, starting at 1
- See the red/green/highlighted/grey circles, corresponding to each of the questions in the game
- See their current score, which should be the same as the score shown on the previous answer screen, or 0 for the first question
- Be able to enter an answer
- After clicking the arrow button, see their last answer - the one that will be submitted
- See a timer that is counting down to zero, after which the user will be redirected to the answer page
- Be able to leave the game, after confirming that's what they want to do

### Solo answer screen
- See the text corresponding to the previous question
- See the correct answer to the question
- Have a feedback text that changes depending on whether the user got the previous question right
- Have the box around these texts change color depending on whether the user gave the right answer
- See the number of the current question, starting at 1
- See the red/green/grey circles, corresponding to each of the questions in the game
- See their current score, which should be the sum of the score shown on the previous question screen and the score the user got for that question
- See a timer that is counting down to zero, after which the user will be redirected to the next question or the results page
- Be able to leave the game, after confirming that's what they want to do

### Solo results screen
- See their own username with their score
- See the top 30 scores in the history of the server, with the usernames associated with them
- See the top 3 users on a podium
- See the red/green circles, corresponding to each of the questions in the game
- Have the option to quit, which redirects them to the home screen
- Have the option to restart the game, which generates 20 questions again

### Waiting screen
- See the number of users currently in the waiting room
- See the usernames of users currently in the waiting room
- Be able to start a multiplayer game
- Be able to return to the home page

### Multiplayer question/estimation screen
- Have all of the functionality of the solo question/estimation screen
- Be able to use the time reduction joker
- Be able to use the double points joker
- Be able to use the disable incorrect answer joker (only for multiple choice questions)
- Have the used jokers disabled for the rest of the game
- Have the disabled incorrect answer visually signified and not clickable (only for multiple choice questions)
- See an icon when they are currently using a joker
- Be able to click emojis to send them to all players in the current game
- See emojis sent by other players in the current game
- Be kicked out when they don't answer three questions in a row

### Multiplayer answer screen
- Have all of the functionality of the solo answer screen
- Be able to click emojis to send them to all players in the current game
- See emojis sent by other players in the current game

### Ranking screen
- See the number of the last question (10)
- See the red/green/grey circles, corresponding to each of the questions in the game
- See a timer that is counting down to zero, after which the user will be redirected to the next question
- See the users in the game with their corresponding scores, ordered by scores
- See the top 3 users on a podium
- Have their own username and score highlighted below the ranking table
- Be able to leave the game, after confirming that's what they want to do

### Multiplayer results screen
- See the number of the last question (20)
- See the red/green circles, corresponding to each of the questions in the game
- See a timer that is counting down to zero, after which the user is unable to sign up for a rematch
- See the users in the game with their corresponding scores, ordered by scores
- See the top 3 users on a podium
- Have their own username and score highlighted below the ranking table
- Be able to quit the game and return to the home screen
- Be able to sign up for a rematch before the timer runs out, at which point they get thrown into a new game
