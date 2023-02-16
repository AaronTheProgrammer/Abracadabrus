How to use Github with Android Studio:

On GitHub:

Log in -> Click on your avatar in the top right hand corner
Choose Settings -> Developer settings -> Personal access tokens
Click on the "Generate new token" button
Add a note if you want, like "Android Studio"
Select repo(all), read:org (under admin:org), gist, workflow
Click on the "Generate token" button
Copy the token


On Android Studio:

Go to File -> Settings -> Version Control -> GitHub -> Add an account
Click on the "Use Token" hyperlink, like in your screenshot
Paste your token, click login, click ok

Then just fumble around with these next instructions

At file menu, you will see the Settings... option. In the Settings view, you go to Version Control, in this menu you'll find the GitHub option, click it.
At host input you should put the URL of the repo; change the Auth Type to Password and put your login and pass; click to Test button, and it's all.

When you want to commit, you can go to VCS menu and click in Commit option.
