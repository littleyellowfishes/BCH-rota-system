# Development Testing

We have been using test driven development, by writing our tests before writing the code. JUnit is the testing framework we have used because of its very simple integration and usability. It allows us to write many tests that each evaluate one atomic mechanism.

During development we may execute one test at a time for whatever we are working on. To fully test the application though we have used bitbucket's pipeline CI feature. This will automatically run every test we have when we commit a change to a selected branch in our bitbucket repository. We also considered and for a short time used CircleCI for this but decided to switch to pipelines. We found that the close integration with bitbucket helped a lot as we could flip back and forth between the repository and pipeline pages easily. We are automatically notified by email if a build fails so we can commit changes and not need to remember to check back in 5 minutes after the tests are done.

We have used automated tests for the backend functionality and manual tests for the front end features. This has the benefit of allowing simple repetitive tests to be automated whilst still letting us evaluate the complex front end.

## Development testing example

Here we have chosen one action, assigning a shift to a user, and have selected a few of our relevant tests. The first two are automated and the third is manual. Each row refers to multiple unit tests that are bundled together in this example.

|Test|Data in DB|Input|Expected result and notes|
| --- | --- | --- | --- |
| addOneShift- verifies that database interaction is working and shifts can be added |None| Blank shift object | Should return a count of 1 shift in the database. This is a simple verification that data can be written to the database and read back to the server. |
| getShiftsByDate – returns a list of all the shifts in the database with the given date |5/1/20:userID=1:shiftType=1 5/1/20:userID=2:shiftType=2 5/1/20:userID=3:shiftType=3 <br>14/1/20:userID=1:shiftType=1 14/1/20:userID=2:shiftType=2 14/1/20:userID=3:shiftType=3| 5/1/20, 4/1/20 | The test requests all the shifts on the given date, 5/1/20. Success is determined by returning exactly the 3 requested shifts. In the case the date is incorrect (4/5/20) no shifts are returned. |
| Log in testing – Seeing if the log in system is working as it should. |Sam:123:Admin <br>Alex:456:User|Sam:123 Alex:456 Jim:abc Jim:123 Alex:abc Alex:123 | This test should allow access on the inputs that are in the database, Sam – 123 and Alex – 456 but deny it on all others. |
