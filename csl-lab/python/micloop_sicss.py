import sicsthsense, miclevel

m = miclevel.Sensor()

d = sicsthsense.Device("sverker.janson@gmail.com", "mylaptop")

while True:
    x = m.getValue(10)
    print "Noise level: "+str(x)
    d.post(['mic'], x)
