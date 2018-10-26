# Script to generate a rotator image using ImageMagick

class Rotator(object):
    
    delay = 15
    width = 64
    height = 32
    fills = ["#999", "#aaa", "#ccc", "#ccc"]
    strokes = ["#666", "#999", "#999", "#aaa"]
    sizes = [[0,0], [1,1], [1,6], [1,10]]
    
    def generate_one(self, index, frame):
        this_frame = frame - index
        if this_frame < 0 or this_frame > 3:
            this_frame = 3
        this_width = int(self.width / 5)
        offset = this_width * index
        spacing = int(self.width / 64) + 1
        inset = [0,0]
        if this_frame == 1:
            inset = [1,1]
        elif this_frame == 2:
            inset = [1, 1 + int(self.height/10)]
        elif this_frame == 3:
            inset = [1, 1 + int(self.height/5)]
        return '-fill "%s" -stroke "%s" -draw "rectangle %d,%d %d,%d"' % (
            self.fills[this_frame], self.strokes[this_frame],
            offset + inset[0], inset[1],
            offset + this_width - inset[0] - spacing, self.height - inset[1])
    
    def generate_frame(self, frame):
        return " \\\n".join( [ "xc:none" ] + [ self.generate_one(i, frame) for i in range(4) ] )
    
    def generate(self):
        print " \\\n".join([
                "convert -dispose previous -delay %d -size %dx%d " % (self.delay, self.width, self.height)
            ] + [
                "\\( %s \\)" % self.generate_frame(f) for f in range(7)
            ] + [
                "-loop 0 out.gif"
            ])

rotator = Rotator()
rotator.generate()
        