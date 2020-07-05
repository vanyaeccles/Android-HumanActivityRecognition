import socket, traceback
import time, os
from datetime import datetime

host = ''
port = 5555
 
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
s.bind((host, port))
 
#used for debugging
 
print("Success binding")


#naming the file
activity_label = input("Please enter a label for the activity: \n")

#get the time as a name
timestr = time.strftime("%d,%m,%Y--%H,%M,%S")

script_dir = os.path.dirname(__file__) #<-- absolute dir the script is in
filename = "Data/" + activity_label + ',' + timestr +".csv"
filename = os.path.join(script_dir, filename)


with open(filename, 'a') as output:
    # write the headers on the top row
    output.write("recieve_time" + "," + "seconds_since_phone_reboot (sensor update)" + "," 
    + "accelerometer_code" + "," + "accelerometer_x" + "," + "accelerometer_y" + ',' + 'accelerometer_z' 
    + ',' + "gyroscope_code" + "," + "gyroscope_x" + "," + "gyroscope_y" + "," + "gyroscope_z"
    + ',' + "magnetometer_code" + "," + "magnetometer_x" + "," + "magnetometer_y" + "," + "magnetometer_z" + '\n')

while 1:
    #get the message as utf text
    message, address = s.recvfrom(8192)
    messageString = message.decode("utf-8")
    #print to console
    print(messageString)   
    print()
    
    
    # save in file named with timestamp
    with open(filename, 'a') as output:
        # current system time to ms precision
        systime = datetime.utcnow().strftime('%H:%M:%S.%f')[:-3]
        output.write(systime + ',' + messageString + ',' + '\n')
 
print("Done streaming, saved to file")

        
        
