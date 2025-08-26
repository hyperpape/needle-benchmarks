def translate_code_points(source, dest, offset):
    assert offset >= 128
    with open(source, 'rb') as f_in, open(dest, 'wb') as f_out:
        while (byte := f_in.read(1)):
            code_point = ord(byte) + offset
            f_out.write(chr(code_point).encode('utf-8'))

translate_code_points('./sherlockholmesascii.txt', './sherlockholmesunicodeoffset.txt', 128)

