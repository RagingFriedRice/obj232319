import sys
import re

#board = [[' ' for i in range(4)] for j in range(4)]
Symbol = "OXABCDEFGHIJKLMNPQRSTUVWYZ"

def DrawBoard(board):
    sys.stdout.write('  ')
    for i in range(len(board)):
        sys.stdout.write(' ' + str(i + 1) + '  ')
    print
    for i in range(len(board)):
        sys.stdout.write(str(i + 1) + ' ')
        for j in range(len(board[i])):
            sys.stdout.write(' ' + board[i][j] + ' ')
            if j < len(board) - 1:
                sys.stdout.write('|')
        print
        sys.stdout.write('  ')
        if i < len(board) - 1:
            for x in range(len(board)):
                sys.stdout.write('---')
                if x < len(board) - 1:
                    sys.stdout.write('+')
        print

def start():
    player_num = 0
    size = 0
    sequence = 0
    while 1:
        usr_input = raw_input("Resume Game? (Y/n) ")
        if usr_input.lower() == 'y':
            usr_input = raw_input("File name? ")
            #TODO: open the file and load into
            # return
        if usr_input.lower() == 'n':
            break
    while 1:
        usr_input = raw_input("Player number? (Maxium 26) ")
        if 0 < int(usr_input) < 26:
            player_num = int(usr_input)
            break
    while 1:
        usr_input = raw_input("Board size? (Maxium 999) ")
        if 2 < int(usr_input) < 999:
            size = int(usr_input)
            break
    while 1:
        usr_input = raw_input("Sequence size? (Smaller than board size) ")
        if 2 < int(usr_input) <= size:
            sequence = int(usr_input)
            break

    #TODO: check the info and report error and quit if not good
    board = [[' ' for i in range(size)] for j in range(size)]
    turn = 0
    return board, player_num, size, sequence, turn

def check(board, row, col, seq, player):
    #check row
    num = 1
    n = row - 1
    while n >= 0:
        if board[n][col] == Symbol[player]:
            num += 1
            n -= 1
        else:
            break
    n = row + 1
    while n < len(board):
        if board[n][col] == Symbol[player]:
            num += 1
            n += 1
        else:
            break
    if num == seq:
        return 1

    #check col
    num = 1
    m = col - 1
    while m >= 0:
        if board[row][m] == Symbol[player]:
            num += 1
            m -= 1
        else:
            break
    m = col + 1
    while m < len(board):
        if board[row][m] == Symbol[player]:
            num += 1
            m += 1
        else:
            break
    if num == seq:
        return 1

    #diag
    num = 1
    n = row - 1
    m = col - 1
    while n >= 0 and m >= 0:
        if board[n][m] == Symbol[player]:
            num += 1
            n -= 1
            m -= 1
        else:
            break
    n = row + 1
    m = col + 1
    while n < len(board) and m < len(board):
        if board[n][m] == Symbol[player]:
            num += 1
            n += 1
            m += 1
        else:
            break
    if num == seq:
        return 1

    #anti-diag
    num = 1
    num = 1
    n = row - 1
    m = col + 1
    while n >= 0 and m < len(board):
        if board[n][m] == Symbol[player]:
            num += 1
            n -= 1
            m += 1
        else:
            break
    n = row + 1
    m = col - 1
    while n < len(board) and m >= 0:
        if board[n][m] == Symbol[player]:
            num += 1
            n += 1
            m -= 1
        else:
            break
    if num == seq:
        return 1

    return 0

testboard = [
            ['O', 'O', 'O'],
            ['X', 'X', ' '],
            [' ', ' ', ' ']
]

DrawBoard(testboard)

if check(testboard, 0, 2, 3, 0):
    print 'O won'
# board, pn, size, sequence, turn = start()

# while 1:
#     DrawBoard(board)
#     player = turn % pn
#     usr_input = raw_input("Player " + str(player + 1) +  " input coordinate or save ")
#     if usr_input == 'Q':
#         #TODO: save and quit
#         break
#     #TODO: regex check
#     if ' ' in usr_input:
#         rc = usr_input.split(' ')
#         row = rc[0] - 1
#         col = rc[1] - 1
#         #TODO: check if the position is valid if not, continue the loop
#         board[row][col] = Symbol[player]
#         if check(board, row, col, sequence, player):
#             print "Player " + str(player + 1) + " won"
#             break
#         # final step
#         turn += 1
