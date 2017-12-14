# Banking-security-transfer
Software shows using encryption/decryption with hashing and keep account information in encryption form for others
INFORMATION SYSTEMS SECURITY
Mobile Application(Android Studio)
MONO Banking System

Brief explanation. When you open an application there is Login Activity, with two functions: sign up and log in. You have
to sign up first, as soon as you signed up you can use your id and password to log in. When you log in, you enter to Main
Activity. Main Activity consists of User Information(user ID and user's current balance), Banking Actions(Deposit,
Withdraw, Transfer), User's Transaction History, Additional User's Functions(delete user's account and change password
of user's acount) and Option Menu which has Customer List and log out. When you press to the Customer List you enter
to the List Activity, which has Current Customer List. If you press to your account you can see your id and balance, but if
you press to another customer then id and garbage will appear. If you press Log out, you log out from your account and
entered back to Login Activity.
In our application we have 3d activities and 2 classes:
Activities:
• Login Activity.In Login Activity we have two main functions log in and sign up. Here
we use Shared Preferenses for Account Information(password, id, salt) and another
Shared Preferenses for initialising balance with encryption. Moreover, we hash
password and save it into the Shared Preferences.• Main Activity. Besides that Main Activity has user's id, user's balance, banking functions (Deposit, Withdraw, Transfer),
and additional functions(delete user and change user's password) it has functions for File Management(Read and Write).
• List Activity. We use map to take all users' ids. For the list of users' we use List View.
When you press to your account, you can see your id and balance. If you press to
another user from the list you can see only his/her id.
Classes:
• AES class
-Encryption and Decryption-Generate key
• Hashing class
-Hash password
Structure of customer info file and customer log file
For Customer Log file we used Shared Preferences. User id is key and salt+hashed password is value.
For customer info file we used Shared Preferences for encrypted balance and .txt file for transaction history.
jdk version: embedded
min sdk version: 26
Additional features of our application:
• Design
Problems we had during developing application.
1. Encryption Decryption. We had an error with an encryption because secret key(salt + password) where longer than 16
bytes.
Solution: we took first 16 bytes from the whole key.
2. Files. At first, we could not create customers' list file and write information there, because whenever we did encryption
when we open file there was an error. We checked without encryption and it was not successful as well.
Solution: we used another method to open file: OutputStreamWriter
3. Transfer. We could not delete user and his balance in temporary file.Transfer money was sending as many times as user
did log in.
Solution: Split temporary file into string array, deleted customer's name and transfered money from this array and rewrite
initial temporary file.
4. History. When we read information from the file and write it into transaction history all text was sticking together. We
could not split infrormation into separate lines.
