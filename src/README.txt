Clasa MyDispatcher
    Am implementat metoda addTask din aceasta clasa, pentru a trimite fiecarui host task-uri in functie de
    algoritmul selectat. Am adaugat variabila de clasa lastHost si am instantiat-o in constructor cu
    numarul de host-uri - 1, folosind-o pentru algoritmul Round Robin.

    In rest, doar verific care este algoritmul folosit si trimit adaug in task-urile host-urilor.

Clasa MyHost
    Aici am identificat problema Producer-Consumer si am utilizat structura PriorityBlockingQueue deoarece ma ajuta
    si pentru prioritatile task-urilor. Mai folosesc o variabila shutdown, care anunta terminarea executiei
    host-ului si executingTask, in care salvam task-ul care se executa la momentul actual de timp.
    In metoda run, extrag task-ul cu prioritatea cea mai mare si incep sa ii scad timpul ramas, simuland astfel
    executia sa, pana cand se termina sau este preemptat. Daca a fost preemptat il adaug inapoi in coada, altfel
    apelez metoda finish a sa. Daca variabila shutdown este setata pe true, iesim din loop.

    In metoda shutdown, setez pe true variabila shutdown, urmand a adauga un element dummy in coada (simulez un
    release pe un semafor care deblocheaza thread-ul care ruleaza run), urmand sa fac join pe thread.

    In getQueueSize calculez lungimea cozii plus a task-ului care ruleaza, iar in getWorkLeft adun toata "munca"
    ramasa pe fiecare task, inclusiv a celui in executie