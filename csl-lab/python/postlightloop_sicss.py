import sicsthsense, camlight, time

print "Read light level using webcam and post to Cosm"

print "Starting light sensor..."
ls = camlight.Sensor()
print "Light sensor started."

d = sicsthsense.Device("sverker.janson@gmail.com", "mylaptop")
print "Feed defined."

while True:
    x = ls.getValue()
    print "Light: "+str(x)
    d.post(['cam'], x)
    time.sleep(10)
