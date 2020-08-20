# -*- coding:utf-8 -*-
import commands
import os
import sys


def main():
    if not os.path.exists('/usr/bin/opencc'):
        raise Exception('opencc is required!')
    if len(sys.argv) < 3:
        raise Exception('from&to is required!')
    from_directory, to_directory = sys.argv[1], sys.argv[2]
    for root, dirs, files in os.walk(from_directory):
        txts = [filename for filename in files if filename.endswith('json')]
        rel = simplify(os.path.relpath(root, from_directory))
        root1 = os.path.join(to_directory, rel)
        if not os.path.exists(root1):
            os.mkdir(root1)
        for txt in txts:
            transform(os.path.join(root, txt), os.path.join(root1, simplify(txt)))


def transform(from_file, to_file):
    cmd = '/usr/bin/opencc -i "%s" -o "%s" -c t2s' % (from_file, to_file)
    print cmd
    os.system(cmd)


def simplify(s):
    cmd = 'echo %s | /usr/bin/opencc -c t2s' % s
    return commands.getoutput(cmd)


if __name__ == '__main__':
    main()