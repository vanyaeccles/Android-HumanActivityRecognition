import socket, traceback
import time, os
from datetime import datetime
import keyboard

#simple script that streams data from port 5555 to the terminal


host = ''
port = 5555
 
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
s.bind((host, port))
 
print("Socket has been set up")


#naming the file
#activity_label = input("Please enter a label for the activity: \n")

#get the time as a name
timestr = time.strftime("%d,%m,%Y--%H,%M,%S")

##script_dir = os.path.dirname(__file__) #<-- absolute dir the script is in
##filename = "Data/" + activity_label + ',' + timestr +".csv"
##filename = os.path.join(script_dir, filename)


while 1:
    if keyboard.is_pressed('q'):#if key 'q' is pressed 
            print('You Pressed A Key!')
            break#finishing the loop
    #get the message as utf text
    message, address = s.recvfrom(8192)
    messageString = message.decode("utf-8")
    #print to console
    print(messageString)   
    print()
    
    

 
print("Done streaming")




        
        
