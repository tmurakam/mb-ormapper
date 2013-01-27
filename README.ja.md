iOS / Android 用 O/R マッパー
=============================

iOS / Android 用の O/R マッパーツールです。
データベース定義 (schema) から自動的にモデルを生成します。

iOS と Android 両対応のアプリを書くときには、特に便利。

ただ、iOS しかアプリ作らない場合は、素直に CoreData を使ったほうが
良いような気がするので、使うのは正直お勧めできないw

注: iOS では ARC 必須です。

モデルの生成手順
----------------

データベース定義のサンプルが sample-schema.rb にあります。
以下のようにすることでモデルを生成できます。

iOS の場合:

    $ ./ormapper-ios sample-schema.rb

Android の場合:

    $ ./ormapper-android sample-schema.rb

生成されたファイルとプロジェクトに追加してください。
また、iOS/Android ディレクトリ内のファイルも適宜追加してください。


使用方法 : iOS の場合
---------------------

最初に以下のようにしてデータベースのシングルトンインスタンスを取得
します。そして open を呼び出してデータベースの作成とオープンを行います。

    Database *db = [Database instance];
    [db open:@"MyDatabase.db"];

次に、各モデルの migrate を呼び出し、テーブルの作成・マイグレーション
を行います。以下はここではモデルクラスが Person の場合の例です。

    [Person migrate];

テーブルの読み込みにはファインダメソッド群 (find) を使います。
これらのメソッドはモデルの NSMutableArray 配列を返します。

    NSMutableArray *people = [Person find_all];

特定の ID を持つレコードを読み出したい場合は find:(int)pid
メソッドを使ってください。

    Person *person = [Person find:1];

レコードの作成、保存を行う場合は、モデルのインスタンスを
生成し、save メソッドを呼ぶだけです。

レコードの削除は delete で行います。


使用方法 : Android の場合
-------------------------

最初に以下のようにしてデータベースの初期化・作成を行います。

    ORDatabase.initialize(this, "MyDatabase.db");

次に、各モデルの migrate を呼び出し、テーブルの作成・マイグレーション
を行います。以下はここではモデルクラスが Person の場合の例です。

    Person.migrate();

テーブルの読み込みにはファインダメソッド群 (find) を使います。
これらのメソッドはモデルの NSMutableArray 配列を返します。

    List<Person> people = Person.find_all();

特定の ID を持つレコードを読み出したい場合は find(int pid)
メソッドを使ってください。

    Person person = Person.find(1);

Rails の ActiveRelation ぽいクエリも可能です。

    List<Person> people = Person.where("age > ?", 20).order("name DESC").all();

    Person person = Person.where("name = ?", name).first();

レコードの作成、保存を行う場合は、モデルのインスタンスを
生成し、save メソッドを呼ぶだけです。

レコードの削除は delete で行います。

schema について
---------------

schema の文法は Ruby on Rails の migration に似た
言語内 DSL になっています。以下にサンプルを示します。

    create_table :people, :class => :Person, :base_class => :PersonBase do |t|
      t.text :name
      t.integer :sex
      t.integer :age
      t.integer :group_id

      t.belongs_to :group, :class => :Group, :field_name => :group_id
    end

テーブル/モデル定義は create_table で行います。
上記の例では 'people' という名前の table が生成されます。
また、:base_class に指定した名前でモデルクラスが生成されます。
開発者は、これを :class に指定した名前のクラスで継承して使用します。

生成されるファイル名は base_class のクラス名で決まります。
上記の例では、iOS では PersonBase.h, PersonBase.m, Android の場合は
PersonBase.java になります。

なお、:base_class を省略すると、:class の指定と同一とみなされます。
:class を省略した場合、テーブル名と同一とみなされます。

テーブル内のフィールドはブロック内に定義します。
メソッド text, integer, long, real, date を使用して、フィールドを定義します。

今のところサポートしている型、およびこれに対応する SQLite, 
Objective-C、および Java の型は以下の通りです。

    type       SQL type   Obj-C type    Java type
    ----------------------------------------------
    integer    INTEGER    int           int
    long       INTEGER    long          long
    real       REAL       double        double
    text       TEXT       NSString *    String
    date       DATE (*1)  NSDate *      long (*2)

    *1: 内部的には14桁の文字列("yyyyMMddHHmmss") となる
    *2: 1970/1/1 0:00 UTC からの経過ミリ秒数


テーブル間のリレーションシップ
------------------------------

リレーションシップも create_table 内に記述します。
1対多の場合は belongs_to, has_many を使用します。

子のテーブル側では belongs_to を指定します。
メソッド名、相手側クラス名、フィールド名を指定してください。
メソッド名で指定したメソッドが自動的に生成されます。

    t.belongs_to :group, :class => :Group, :field_name => :group_id

親のテーブル側では、has_many を指定します。
名前、相手側クラス名、相手側テーブルのフィールド名を指定してください。
メソッド名で指定したメソッドが自動的に生成されます (返り値は List
になります)

    t.has_many :people, :class => :Person, :field_name => :group_id

なお、1対1関係の場合は、has_many の代わりに has_one を使用して下さい。

テーブルの migrate について
---------------------------

schema にフィールドを追加した場合、migrate メソッドを呼び出した
ときに自動的にカラムが追加されます。

ただし、既存のフィールドを変更したり途中のフィールドを変更した
場合は対応できませんので、注意してください。


ライセンスについて
------------------

libs 以下のファイルは BSD ライセンスに準じます。
詳細は LICENSE ファイルを見て下さい。

本ジェネレータで生成されたソースコードについては、ライセンス条件
は一切つきません (Public Domain とお考えください)

TODO
----

- テストクラスの自動生成

